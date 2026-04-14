package com.rngad33.ark.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.rngad33.ark.constant.ThumbConstant;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.service.ThumbService;
import com.rngad33.ark.utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 定时任务：同步点赞信息
 */
@Component
public class SyncThumb2DBTasks {

    @Resource
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 同步点赞信息到数据库
     */
    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void doSynchronize() {
        // 扫描 Redis 查找点赞信息
        String date = this.alignTime();
        Map<Object, Object> thumbMap = redisTemplate.opsForHash().entries(RedisKeyUtils.getTempThumbKey(date));
        if (CollUtil.isEmpty(thumbMap)) {
            return;
        }
        Map<Long, Long> thumbCountMap = new HashMap<>();
        List<Thumb> thumbs = new ArrayList<>();
        for (Object userIdPictureIdObj : thumbMap.keySet()) {
            String userIdPictureId = (String) userIdPictureIdObj;
            String[] split = userIdPictureId.split(":");
            Long userId = Long.parseLong(split[0]);
            Long pictureId = Long.parseLong(split[1]);

        }
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
        String date = DateUtil.format(nowDate, "HH:MM:" + second);
        return date;
    }

}