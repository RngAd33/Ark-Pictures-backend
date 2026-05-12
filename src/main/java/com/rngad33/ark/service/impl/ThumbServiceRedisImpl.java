package com.rngad33.ark.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.rngad33.ark.constant.RedisLuaScriptConstant;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
import com.rngad33.ark.model.enums.misc.LuaStatusEnum;
import com.rngad33.ark.utils.RedisKeyUtils;
import com.rngad33.ark.utils.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 点赞业务实现类增强（高并发）
 */
@Service
@ConditionalOnProperty(name="sky.concurrency.enable", havingValue = "true")
public class ThumbServiceRedisImpl extends ThumbServiceImpl {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 点赞（重写）
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean doThumb(ThumbRequest thumbRequest) {
        ThrowUtils.throwIf(ObjUtil.isNull(thumbRequest), ErrorCodeEnum.PARAMS_ERROR, "无效的请求！");
        ThrowUtils.throwIf(this.hasThumb(thumbRequest.getPictureId(), thumbRequest.getUserId()),
                ErrorCodeEnum.USER_LOSE_ACTION, "请勿重复点赞！");
        String timeSlice = this.getTimeSlice();
        String userThumbKey = RedisKeyUtils.getUserThumbKey(thumbRequest.getUserId());
        String tempThumbKey = RedisKeyUtils.getTempThumbKey(timeSlice);

        // 执行 Lua 脚本
        long result = redisTemplate.execute(RedisLuaScriptConstant.THUMB_SCRIPT,
                Arrays.asList(tempThumbKey, userThumbKey),
                thumbRequest.getUserId(), thumbRequest.getPictureId());
        ThrowUtils.throwIf(result == LuaStatusEnum.FAIL.getValue(), ErrorCodeEnum.USER_LOSE_ACTION, "点赞失败！");

        return result == LuaStatusEnum.SUCCESS.getValue();
    }

    /**
     * 取消点赞（重写）
     *
     * @param thumbRequest
     * @return
     */
    @Override
    public boolean unThumb(ThumbRequest thumbRequest) {
        ThrowUtils.throwIf(ObjUtil.isNull(thumbRequest), ErrorCodeEnum.PARAMS_ERROR, "无效的请求！");
        ThrowUtils.throwIf(this.hasThumb(thumbRequest.getPictureId(), thumbRequest.getUserId()),
                ErrorCodeEnum.USER_LOSE_ACTION, "请勿重复点赞！");
        String timeSlice = this.getTimeSlice();
        String userThumbKey = RedisKeyUtils.getUserThumbKey(thumbRequest.getUserId());
        String tempThumbKey = RedisKeyUtils.getTempThumbKey(timeSlice);

        // 执行 Lua 脚本
        long result = redisTemplate.execute(RedisLuaScriptConstant.UNTHUMB_SCRIPT,
                Arrays.asList(tempThumbKey, userThumbKey),
                thumbRequest.getUserId(), thumbRequest.getPictureId());
        ThrowUtils.throwIf(result == LuaStatusEnum.FAIL.getValue(), ErrorCodeEnum.USER_LOSE_ACTION, "点赞失败！");

        return result == LuaStatusEnum.SUCCESS.getValue();
    }

    /**
     * 获取时间片
     *
     * @return
     */
    private String getTimeSlice() {
        DateTime now = DateTime.now();
        // 获取到当前时间前最近的整数秒，比如当前 11：20：23，获取到 11:20：20
        return DateUtil.format(now, "HH:mm") + (DateUtil.second(now) / 10) * 10;
    }

}