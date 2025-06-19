package com.rngad33.web.manager.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Caffeine本地缓存模板
 */
@Service
public class CacheTemplateByCaffeine extends CacheTemplate {

    /**
     * 本地缓存构造
     */
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10_000L)   // 最多缓存1000条数据
            .expireAfterAccess(Duration.ofMinutes(5))   // 缓存5分钟后清除
            .build();

    /**
     * 获取缓存数据
     *
     * @param hashKey
     * @return cachedValue
     */
    @Override
    protected String getCachedValue(String hashKey) {
        String caffeineKey = String.format("listPictureVOByPage:%s", hashKey);
        return LOCAL_CACHE.getIfPresent(caffeineKey);
    }

    /**
     * 将数据写入缓存
     */
    protected void setCache(String hashKey, String cacheValue, int cacheExpireTime) {
        LOCAL_CACHE.put(cacheValue, cacheValue);
    }

}