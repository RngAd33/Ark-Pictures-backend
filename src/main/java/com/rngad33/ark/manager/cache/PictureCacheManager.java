package com.rngad33.ark.manager.cache;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mybatisflex.core.paginate.Page;
import com.rngad33.ark.model.dto.picture.PictureQueryRequest;
import com.rngad33.ark.model.entity.Picture;
import com.rngad33.ark.model.vo.PictureVO;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.utils.LockUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 图片二级缓存读写策略
 */
@Component
@Slf4j
public class PictureCacheManager {

    @Resource
    private PictureService pictureService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RBloomFilter<String> bloomFilter;

    /**
     * 本地缓存构造
     */
    private final Cache<String, Object> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10_000L)   // 最多缓存10000条数据
            .expireAfterAccess(Duration.ofMinutes(5))   // 缓存5分钟后清除
            .build();

    private PictureCacheManager() {}   // 私有构造函数，防止外部实例化破坏单例模式

    /**
     * 数据多级查询
     *
     * @param pictureQueryRequest 查询请求
     * @param redisKey 缓存的 key
     * @param caffeineKey 缓存的 key
     * @param current 当前页
     * @param size 页尺寸
     * @param request HTTP请求
     * @return
     */
    public Page<PictureVO> cacheQuery(PictureQueryRequest pictureQueryRequest, String redisKey, String caffeineKey,
                                      long current, long size, HttpServletRequest request) {
        // 使用布隆过滤器判断 key 是否存在
        if (ObjUtil.isNotNull(pictureQueryRequest) && !bloomFilter.contains(redisKey)) {
            // key不存在，直接返回空页面，避免缓存穿透
            return new Page<>(current, size);
        }
        // 优先查询本地缓存
        String cachedValue = (String) LOCAL_CACHE.getIfPresent(caffeineKey);
        if (cachedValue == null) {
            // - 本地缓存未命中，查询Redis缓存
            cachedValue = (String) redisTemplate.opsForValue().get(redisKey);
            if (cachedValue == null) {
                // 双检锁
                // Object lock = LockUtils.KEY_LOCK.computeIfAbsent(redisKey, k -> new Object());
                synchronized (LockUtils.getKeyLock(redisKey)) {
                    cachedValue = (String) redisTemplate.opsForValue().get(redisKey);
                    if (cachedValue == null) {
                        // - 两种缓存均未命中，查询数据库
                        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                                pictureService.getQueryWrapper(pictureQueryRequest));
                        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);
                        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);   // 序列化
                        // 设置 Redis 缓存有效期
                        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);   // 预留区间，防止缓存雪崩
                        // 写入二级缓存
                        LOCAL_CACHE.put(caffeineKey, cacheValue);
                        redisTemplate.opsForValue().set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
                        // 返回数据库查询结果
                        return pictureVOPage;
                    }
                }
            } else {
                // - Redis缓存命中，写入本地缓存
                LOCAL_CACHE.put(caffeineKey, cachedValue);
            }
        }
        // 二级缓存命中，返回缓存查询结果
        return JSONUtil.toBean(cachedValue, Page.class);   // 反序列化
    }

}