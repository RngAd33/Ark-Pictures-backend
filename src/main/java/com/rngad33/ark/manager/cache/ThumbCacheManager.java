package com.rngad33.ark.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 点赞二级缓存读写策略
 */
@Component
@Slf4j
public class ThumbCacheManager {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final TopK hotKeyDetector = new HeavyKeeper(100, 1000, 100, 0.999, 2);

    /**
     * 本地缓存构造
     */
    private final Cache<String, Object> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10_000L)   // 最多缓存10000条数据
            .expireAfterAccess(Duration.ofMinutes(5))   // 缓存5分钟后清除
            .build();

    /**
     * 从二级缓存中获取数据
     *
     * @param hashKey
     * @param key
     * @return
     */
    public Object get(String hashKey, String key) {
        // 构造唯一的 composite key
        String compositeKey = this.buildCacheKey(hashKey, key);

        // 1. 先查本地缓存
        Object value = LOCAL_CACHE.getIfPresent(compositeKey);
        if (value != null) {
            log.info("本地缓存获取到数据 {} = {}", compositeKey, value);
            // 记录访问次数（每次访问计数 +1）
            hotKeyDetector.add(key, 1);
            return value;
        }

        // 2. 本地缓存未命中，查询 Redis
        Object redisValue = redisTemplate.opsForHash().get(hashKey, key);
        if (redisValue == null) {
            return null;
        }

        // 3. 记录访问（计数 +1）
        AddResult addResult = hotKeyDetector.add(key, 1);

        // 4. 如果是热 Key 且不在本地缓存，则缓存数据
        if (addResult.isHotKey()) {
            LOCAL_CACHE.put(compositeKey, redisValue);
        }

        return redisValue;
    }

    /**
     * 缓存数据到 Caffeine
     *
     * @param hashKey
     * @param key
     * @param value
     */
    public void putIfPresent(String hashKey, String key, Object value) {
        String compositeKey = this.buildCacheKey(hashKey, key);
        Object object = LOCAL_CACHE.getIfPresent(compositeKey);
        if (object == null) {
            return;
        }
        LOCAL_CACHE.put(compositeKey, value);
    }

    /**
     * 定时清理过期的热 Key 检测数据
     */
    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    public void cleanHotKeys() {
        hotKeyDetector.fading();
    }

    /**
     * 辅助方法：构造复合 key
     *
     * @param hashKey
     * @param key
     * @return
     */
    private String buildCacheKey(String hashKey, String key) {
        return hashKey + ":" + key;
    }

}
