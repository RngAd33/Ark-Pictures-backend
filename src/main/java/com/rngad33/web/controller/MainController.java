package com.rngad33.web.controller;

import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.common.ResultUtils;
import com.rngad33.web.constant.UserConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局通用接口
 */
@Slf4j
@RestController
@RequestMapping("/")
public class MainController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 清空缓存（仅管理员）
     *
     * @param redisKey
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/cleanCache")
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