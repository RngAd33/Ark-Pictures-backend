package com.rngad33.ark.exception;

import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
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
     * 具体描述（可选）
     */
    private final String des;

    /**
     * 手动传参
     *
     * @param code
     * @param msg
     * @param des
     */
    public MyException(int code, String msg, String des) {
        this.code = code;
        this.msg = msg;
        this.des = des;
    }

    /**
     * 半自动传参（自行输入描述）
     *
     * @param errorCode
     * @param des 具体描述
     */
    public MyException(ErrorCodeEnum errorCode, String des) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
        this.des = des;
    }

    /**
     * 自动传参（无描述）
     *
     * @param errorCode
     */
    public MyException(ErrorCodeEnum errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
        this.des = null;
    }

}