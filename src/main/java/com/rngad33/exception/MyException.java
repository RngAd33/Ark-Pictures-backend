package com.yupi.usercenter.exception;

import com.yupi.usercenter.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 自定义异常类
 */
@Getter
public class MyException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 报错信息
     */
    private final String msg;


    /**
     * 手动传参
     *
     * @param msg
     * @param code
     */
    public MyException(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    /**
     * 自动传参
     *
     * @param errorCode
     */
    public MyException(ErrorCodeEnum errorCode) {
        this.msg = errorCode.getMsg();
        this.code = errorCode.getCode();
    }

    /**
     * 半自动传参
     *
     * @param msg 自定义错误消息
     * @param errorCode
     */
    public MyException(String msg, ErrorCodeEnum errorCode) {
        this.msg = msg;
        this.code = errorCode.getCode();
    }

}