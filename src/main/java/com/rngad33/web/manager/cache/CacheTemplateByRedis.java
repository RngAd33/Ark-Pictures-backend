package com.rngad33.web.manager.cache;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存模板
 */
@Service
public class CacheTemplateByRedis extends CacheTemplate {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取缓存数据
     *
     * @return cachedValue
     */
    @Override
    protected String getCachedValue(String hashKey) {
        String redisKey = String.format("picture:listPictureVOByPage:vo:%s", hashKey);
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 将数据写入缓存
     */
    protected void setCache(String hashKey, String cacheValue, int cacheExpireTime) {
        String redisKey = String.format("picture:listPictureVOByPage:vo:%s", hashKey);
        stringRedisTemplate.opsForValue().set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
    }

}