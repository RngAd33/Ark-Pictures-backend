package com.rngad33.ark.utils;

import com.rngad33.ark.constant.PictureConstant;
import com.rngad33.ark.constant.ThumbConstant;

/**
 * Redis key 工具类
 */
public class RedisKeyUtils {

    /**
     * 获取图片点赞记录 key
     *
     * @param pictureId
     * @return
     */
    public static String getPictureThumbKey(Long pictureId) {
        return PictureConstant.PICTURE_THUMB_PREFIX + pictureId;
    }

    /**
     * 获取图片取消点赞记录 key
     *
     * @param pictureId
     * @return
     */
    public static String getPictureUnThumbKey(Long pictureId) {
        return PictureConstant.PICTURE_UNTHUMB_PREFIX + pictureId;
    }

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