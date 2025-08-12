package com.rngad33.ark.manager;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rngad33.ark.model.dto.picture.PictureQueryRequest;
import com.rngad33.ark.model.entity.Picture;
import com.rngad33.ark.model.vo.PictureVO;
import com.rngad33.ark.service.PictureService;
import com.rngad33.ark.utils.LockUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 通用二级缓存读写策略
 */
@Service
public class MyCacheManager {

    @Resource
    private PictureService pictureService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private RBloomFilter<String> bloomFilter;

    /**
     * 本地缓存构造
     */
    private final Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10_000L)   // 最多缓存10000条数据
            .expireAfterAccess(Duration.ofMinutes(5))   // 缓存5分钟后清除
            .build();

    /**
     * 初始化布隆过滤器
     */
    @PostConstruct
    public void initBloomFilter() {
        bloomFilter = redissonClient.getBloomFilter("picture_bloom_filter");
        // 设置预期插入数量和误判率
        bloomFilter.tryInit(1000000, 0.01);
    }

    /**
     * 数据多级查询
     *
     * @param pictureQueryRequest 查询请求
     * @param redisKey 缓存的key
     * @param caffeineKey 缓存的key
     * @param current 当前页
     * @param size 页尺寸
     * @param request HTTP请求
     * @return
     */
    public Page<PictureVO> cacheQuery(PictureQueryRequest pictureQueryRequest, String redisKey, String caffeineKey,
                                      long current, long size, HttpServletRequest request) {
        // 使用布隆过滤器判断key是否存在
        if (ObjUtil.isNotNull(pictureQueryRequest) && !bloomFilter.contains(redisKey)) {
            // key不存在，直接返回空页面，避免缓存穿透
            return new Page<PictureVO>().setSize(size).setCurrent(current);
        }
        // 优先查询本地缓存
        String cachedValue = getCachedFromCaffeine(redisKey);
        if (cachedValue == null) {
            // - 本地缓存未命中，查询Redis缓存
            cachedValue = getCachedFromRedis(caffeineKey);
            if (cachedValue == null) {
                // 设置双检锁
                // Object lock = LockUtils.KEY_LOCKS.computeIfAbsent(redisKey, k -> new Object());
                synchronized (LockUtils.getKeyLock(redisKey)) {
                    cachedValue = getCachedFromRedis(caffeineKey);
                    if (cachedValue == null) {
                        // - 两种缓存均未命中，查询数据库
                        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                                pictureService.getQueryWrapper(pictureQueryRequest));
                        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);
                        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);   // 序列化
                        // 设置Redis缓存有效期
                        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);   // 预留区间，防止缓存雪崩
                        // 写入二级缓存
                        this.setCacheToRedis(redisKey, cacheValue, cacheExpireTime);
                        this.setCacheToCaffeine(caffeineKey, cacheValue);
                        // 返回数据库查询结果
                        return pictureVOPage;
                    }
                }
            } else {
                // - Redis缓存命中，写入本地缓存
                this.setCacheToCaffeine(caffeineKey, cachedValue);
            }
        }
        // 二级缓存命中，返回缓存查询结果
        return JSONUtil.toBean(cachedValue, Page.class);   // 反序列化
    }

    /**
     * 从Redis缓存中获取缓存数据
     *
     * @param redisKey 缓存的key
     * @return cachedValue
     */
    private String getCachedFromRedis(String redisKey) {
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 从本地缓存中获取缓存数据
     *
     * @param caffeineKey 缓存的key
     * @return cachedValue
     */
    private String getCachedFromCaffeine(String caffeineKey) {
        return LOCAL_CACHE.getIfPresent(caffeineKey);
    }

    /**
     * 将数据写入Redis缓存
     *
     * @param redisKey 缓存的key
     * @param cacheValue 缓存的数据
     * @param cacheExpireTime 超时时间
     */
    private void setCacheToRedis(String redisKey, String cacheValue, int cacheExpireTime) {
        stringRedisTemplate.opsForValue().set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
    }

    /**
     * 将数据写入本地缓存
     *
     * @param caffeineKey
     * @param cacheValue
     */
    private void setCacheToCaffeine(String caffeineKey, String cacheValue) {
        LOCAL_CACHE.put(caffeineKey, cacheValue);
    }

}