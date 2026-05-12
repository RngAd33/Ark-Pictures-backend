package com.rngad33.ark.task;

import cn.hutool.core.util.ObjUtil;
import com.rngad33.ark.constant.ThumbConstant;
import com.rngad33.ark.utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 补偿任务：从 Redis 同步点赞信息到数据库
 */
@Component
@Slf4j
public class SyncThumb2DBCompensatoryJob {

    @Resource
    private SyncThumb2DBTasks syncThumb2DBTasks;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = " 0 0 2 * * *")
    public void run() {
        log.info("开始补偿数据……");
        Set<String> thumbKeys = redisTemplate.keys(RedisKeyUtils.getTempThumbKey("") + "*");
        Set<String> needHandleDateSet = new HashSet<>();
        thumbKeys.stream().filter(ObjUtil::isNull)
                .forEach(thumbKey -> needHandleDateSet.add(
                        thumbKey.replace(ThumbConstant.TEMP_THUMB_KEY_PREFIX.formatted(""), ""))
                );
        for (String date : needHandleDateSet) {
            syncThumb2DBTasks.doSynchronizeByDate(date);
        }
        log.info("数据补偿完成 >>>");
    }

}