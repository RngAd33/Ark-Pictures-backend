package com.rngad33.ark.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.rngad33.ark.manager.MyCacheManager;
import com.rngad33.ark.mapper.ThumbMapper;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.service.ThumbService;
import com.rngad33.ark.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 点赞服务实现类
 */
@Service
@Slf4j
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    @Resource
    private MyCacheManager myCacheManager;

    @Resource
    private UserService userService;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 点赞
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean doThumb(ThumbRequest thumbRequest) {
        long userId = thumbRequest.getUserId();
        long pictureId = thumbRequest.getPictureId();

        // 判断是否已经点赞
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", userId).eq("pictureId", pictureId);
        if (this.count(queryWrapper) > 0) {
            log.error("你已点赞过该图片！");
            return false;
        }

        // 加分布式锁，执行操作


        return true;
    }

    /**
     * 取消点赞
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean unThumb(ThumbRequest thumbRequest) {
        long userId = thumbRequest.getUserId();
        long pictureId = thumbRequest.getPictureId();

        return false;
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
        queryWrapper.select("id").eq("userId", userId);
        List<Thumb> list = this.list(queryWrapper);
        return list.stream().map(Thumb::getId).collect(Collectors.toList());
    }

    /**
     * 统计某图片的点赞量
     *
     * @param pictureId
     * @return
     */
    @Override
    public long countThumb(long pictureId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("pictureId", pictureId);
        return this.count(queryWrapper);
    }

}