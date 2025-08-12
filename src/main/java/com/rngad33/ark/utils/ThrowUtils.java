package com.rngad33.ark.utils;

import com.rngad33.ark.exception.MyException;
import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;

/**
 * 异常抛出工具类
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCodeEnum errorCode) {
        throwIf(condition, new MyException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void throwIf(boolean condition, ErrorCodeEnum errorCode, String message) {
        throwIf(condition, new MyException(errorCode, message));
    }

}