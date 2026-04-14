package com.rngad33.ark.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rngad33.ark.constant.ThumbConstant;
import com.rngad33.ark.exception.MyException;
import com.rngad33.ark.manager.LockManager;
import com.rngad33.ark.mapper.PictureMapper;
import com.rngad33.ark.mapper.ThumbMapper;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.Picture;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.service.ThumbService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.rngad33.ark.model.entity.table.ThumbTableDef.THUMB;

/**
 * 点赞服务实现类
 */
@Service
@Slf4j
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    @Resource
    private LockManager lockManager;

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 点赞
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean doThumb(ThumbRequest thumbRequest) {
        Long userId = thumbRequest.getUserId();
        long pictureId = thumbRequest.getPictureId();
        // 加分布式锁，执行操作
        try {
            boolean hasLocked = lockManager.tryLock("doThumb:" + userId, 30000, 600000 + RandomUtil.randomInt(100000), TimeUnit.MILLISECONDS);
            if (hasLocked) {
                // 开启编程式事务
                return Objects.equals(Boolean.TRUE, transactionTemplate.execute(status -> {
                    // 判断是否已经点赞
                    QueryWrapper queryWrapper = QueryWrapper.create()
                            .eq(THUMB.PICTURE_ID.getName(), pictureId)
                            .eq(THUMB.USER_ID.getName(), userId);
                    if (this.count(queryWrapper) > 0) {
                        log.error("你已点赞过该图片！");
                        throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION, "你已点赞过该图片！");
                    }
                    // 更新图片点赞数 + 1
                    Picture picture = pictureMapper.selectOneById(pictureId);
                    picture.setThumbCount(picture.getThumbCount() + 1);
                    int update = pictureMapper.update(picture);
                    if (update == 1) {
                        // 保存点赞记录到数据库
                        Thumb thumb = new Thumb();
                        thumb.setUserId(userId);
                        thumb.setPictureId(pictureId);
                        redisTemplate.opsForHash().put(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, pictureId, thumb.getId());
                        log.info("点赞成功！");
                        return true;
                    } else {
                        log.error("点赞量数据未同步！");
                        return false;
                    }
                }));
            } else {
                log.error("获取分布式锁失败！");
                return false;
            }
        } finally {
            // 不论是否成功都要释放锁
            lockManager.unlock("doThumb:" + userId);
        }
    }

    /**
     * 取消点赞
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean unThumb(ThumbRequest thumbRequest) {
        Long userId = thumbRequest.getUserId();
        long pictureId = thumbRequest.getPictureId();
        // 加分布式锁，执行操作
        try {
            boolean hasLocked = lockManager.tryLock("unThumb:" + userId, 30000, 600000 + RandomUtil.randomInt(100000), TimeUnit.MILLISECONDS);
            if (hasLocked) {
                // 开启编程式事务
                return Objects.equals(Boolean.TRUE, transactionTemplate.execute(status -> {
                    // 判断是否已经点赞
                    QueryWrapper queryWrapper = QueryWrapper.create()
                            .eq(THUMB.PICTURE_ID.getName(), pictureId)
                            .eq(THUMB.USER_ID.getName(), userId);
                    if (this.count(queryWrapper) == 0) {
                        log.error("你还没有点赞过该图片！");
                        throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION, "你还没有点赞过该图片！");
                    }
                    // 更新图片点赞数 - 1
                    Picture picture = pictureMapper.selectOneById(pictureId);
                    picture.setThumbCount(picture.getThumbCount() - 1);
                    int update = pictureMapper.update(picture);
                    // 删除点赞记录
                    boolean success = (update == 1) && this.remove(queryWrapper);
                    if (success) {
                        redisTemplate.opsForHash().delete(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, pictureId);
                        log.info("取消点赞成功！");
                    }
                    return success;
                }));
            }
        } finally {
            // 不论是否成功都要释放锁
            lockManager.unlock("unThumb:" + userId);
        }
        return true;
    }

    /**
     * 查看某用户赞过的图片 id
     *
     * @param userId
     * @return
     */
    @Override
    public List<Long> listThumbIds(long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(THUMB.PICTURE_ID).eq(THUMB.USER_ID.getName(), userId);
        List<Thumb> list = this.list(queryWrapper);
        return list.stream().map(Thumb::getId).collect(Collectors.toList());
    }

    /**
     * 统计某用户的累计获赞量
     *
     * @param userId
     * @return
     */
    @Override
    public long countUserThumb(long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        return this.count(queryWrapper);
    }

    @Override
    public long countPictureThumb(long pictureId) {
        return 0;
    }

    /**
     * 判断是否已点赞
     *
     * @param pictureId
     * @param userId
     * @return
     */
    @Override
    public boolean hasThumb(long pictureId, long userId) {
        return redisTemplate.opsForHash().hasKey(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, pictureId);
    }

}