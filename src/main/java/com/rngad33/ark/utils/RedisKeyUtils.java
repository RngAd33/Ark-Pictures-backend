package com.rngad33.ark.utils;

import com.rngad33.ark.constant.ThumbConstant;

public class RedisKeyUtils {

    /**
     * 获取用户点赞记录 key
     *
     * @param userId
     * @return
     */
    public static String getUserThumbKey(Long userId) {
        return ThumbConstant.USER_THUMB_KEY_PREFIX + userId;
    }

    /**
     * 获取临时点赞记录 key
     *
     * @param time
     * @return
     */
    public static String getTempThumbKey(String time) {
        return ThumbConstant.TEMP_THUMB_KEY_PREFIX.formatted(time);
    }

}