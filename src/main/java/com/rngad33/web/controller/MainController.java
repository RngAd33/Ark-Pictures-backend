package com.rngad33.web.controller;

import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.common.ResultUtils;
import com.rngad33.web.manager.MyCacheManager;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 全局通用接口
 */
public class MainController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 清空缓存
     *
     * @param redisKey
     * @return
     */
    public BaseResponse<Integer> removeCache(String redisKey) {
        stringRedisTemplate.delete(redisKey);
        return ResultUtils.success(null);
    }

    /**
     * Ping接口
     */
    @PostMapping("/ping")
    public void ping() {
        System.out.println("OK>>>");
    }

}