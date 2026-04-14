package com.rngad33.ark.task;

import com.rngad33.ark.constant.ThumbConstant;
import com.rngad33.ark.model.entity.Thumb;
import com.rngad33.ark.service.ThumbService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 定时任务：同步点赞信息
 */
@Component
public class SyncThumb2DBTasks {

    @Resource
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 设置最大容量
    private static final int MAX = 200;

    /**
     * 同步点赞信息到数据库
     */
    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void doSynchronize() {
        List<Thumb> thumbsNeedSave = new LinkedList<>();
        // 扫描 Redis 查找点赞信息
        Cursor<String> cursor = redisTemplate.scan(
                ScanOptions.scanOptions()
                        .match(ThumbConstant.USER_THUMB_KEY_PREFIX + "*")
                        .count(MAX)
                        .build()
        );
        // 存入临时列表并删除 Redis 中的数据
        while (cursor.hasNext()) {
            String key = cursor.next();
            Thumb thumbNeedSave = (Thumb) redisTemplate.opsForValue().get(key);
            thumbsNeedSave.add(thumbNeedSave);
            redisTemplate.delete(key);
        }
        // 判断临时列表是否为空
        if (!thumbsNeedSave.isEmpty()) {
            // - 查找到信息，执行批量保存
            thumbService.saveBatch(thumbsNeedSave);
        }
    }

    /**
     * 同步取消点赞信息到数据库
     */
    @Scheduled(fixedRate = 600000)
    @Transactional(rollbackFor = Exception.class)
    public void doUnSynchronize() {
        List<Long> thumbIdsNeedRemove = new LinkedList<>();
        // 扫描 Redis 查找取消点赞信息
        Cursor<String> cursor = redisTemplate.scan(
                ScanOptions.scanOptions()
                        .match(ThumbConstant.TEMP_THUMB_KEY_PREFIX + "*")
                        .count(MAX)
                        .build()
        );
        // 存入临时列表并删除 Redis 中的数据
        while (cursor.hasNext()) {
            String key = cursor.next();
            Thumb thumbNeedRemove = (Thumb) redisTemplate.opsForValue().get(key);
            thumbIdsNeedRemove.add(thumbNeedRemove.getId());
            redisTemplate.delete(key);
        }
        // 判断临时列表是否为空
        if (!thumbIdsNeedRemove.isEmpty()) {
            // - 查找到信息，执行批量删除
            thumbService.removeByIds(thumbIdsNeedRemove);
        }
    }

}