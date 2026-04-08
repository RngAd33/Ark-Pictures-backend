package com.rngad33.ark.task;

import com.rngad33.ark.service.ThumbService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务
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
    @Scheduled(fixedRate = 500000)
    @Transactional(rollbackFor = Exception.class)
    public void doSynchronize(){

    }

}