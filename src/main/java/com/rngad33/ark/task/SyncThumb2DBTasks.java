package com.rngad33.ark.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import com.mybatisflex.core.query.QueryWrapper;
import com.rngad33.ark.constant.ThumbConstant;
import com.rngad33.ark.mapper.PictureMapper;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
import com.rngad33.ark.model.enums.thumb.ThumbTypeEnum;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.service.ThumbService;
import com.rngad33.ark.utils.RedisKeyUtils;
import com.rngad33.ark.utils.ThrowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.rngad33.ark.model.entity.table.ThumbTableDef.THUMB;

/**
 * 定时任务：同步点赞信息
 */
@Component
@Slf4j
public class SyncThumb2DBTasks {

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 同步点赞信息到数据库
     */
    @Scheduled(fixedRate = 30000)
    @Transactional(rollbackFor = Exception.class)
    public void doSynchronize() {
        // 扫描 Redis 查找点赞信息
        String date = this.alignTime();
        String thumbKey = RedisKeyUtils.getTempThumbKey(date);
        Map<Object, Object> thumbMap = redisTemplate.opsForHash().entries(thumbKey);
        if (CollUtil.isEmpty(thumbMap)) {
            return;
        }
        Map<Long, Long> thumbCountMap = new HashMap<>();
        List<Thumb> thumbs = new ArrayList<>();
        QueryWrapper queryWrapper = new QueryWrapper();
        boolean needDelete = false;
        for (Object userIdPictureIdObj : thumbMap.keySet()) {
            String userIdPictureId = (String) userIdPictureIdObj;
            String[] split = userIdPictureId.split(StrPool.COLON);
            Long userId = Long.parseLong(split[0]);
            Long pictureId = Long.parseLong(split[1]);
            // 判断是点赞还是取消点赞
            int thumbType = Integer.valueOf(thumbCountMap.get(userIdPictureId).toString());
            if (thumbType == ThumbTypeEnum.INCR.getValue()) {
                // - 点赞
                Thumb thumb = new Thumb();
                thumb.setUserId(userId);
                thumb.setPictureId(pictureId);
                thumbs.add(thumb);
            } else if (thumbType == ThumbTypeEnum.DECR.getValue()) {
                // - 取消点赞
                needDelete = true;
                queryWrapper.or(THUMB.USER_ID.eq(userId)).eq(THUMB.PICTURE_ID.getName(), pictureId);  // todo 可能存在语句错误
            } else {
                if (thumbType != ThumbTypeEnum.NON.getValue()) {
                    // - 状态异常
                    log.warn("数据异常：{}", userId + "," + pictureId + "," + thumbType);
                }
                continue;
            }
            // 计算点赞增量
            thumbCountMap.put(pictureId, thumbCountMap.getOrDefault(pictureId, 0L) + thumbType);
        }
        // 批量插入
        thumbService.saveBatch(thumbs);
        // 批量删除
        if (needDelete) {
            thumbService.remove(queryWrapper);
        }
        // 批量更新图片点赞量
        if (!thumbCountMap.isEmpty()) {
            pictureMapper.batchUpdateThumbCount(thumbCountMap);
        }
        // 异步删除
        Thread.startVirtualThread(() -> {
            redisTemplate.delete(thumbKey);
        });
    }

    /**
     * 时间校准
     *
     * @return
     */
    private String alignTime() {
        Date nowDate = DateUtil.date();
        int second = (DateUtil.second(nowDate) / 10 - 1) * 10;
        if (second == -10) {
            second = 50;
            // 回到上一分钟
            nowDate = DateUtil.offsetMinute(nowDate, -1);
        }
        return DateUtil.format(nowDate, "HH:MM:" + second);
    }

}