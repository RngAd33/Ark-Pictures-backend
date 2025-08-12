package com.rngad33.ark.controller;

import com.rngad33.ark.annotation.AuthCheck;
import com.rngad33.ark.common.BaseResponse;
import com.rngad33.ark.constant.UserConstant;
import com.rngad33.ark.utils.ResultUtils;
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
    @PostMapping("/removeCache")
    public BaseResponse<Integer> removeCache(String redisKey) {
        stringRedisTemplate.delete(redisKey);
        return ResultUtils.success(null);
    }

}