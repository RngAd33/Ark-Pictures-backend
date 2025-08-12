package com.rngad33.ark.model.enums.misc;

import lombok.Getter;

/**
 * 自定义异常枚举
 */
@Getter
public enum ErrorCodeEnum {

    SUCCESS("OK >>>", 0),
    USER_NOT_EXIST_OR_PASSWORD_ERROR_RETRY("——！用户不存在或密码错误，请重试！——", 4042),
    USER_TOO_MANY_TIMES("——！请求超时！——", 5000),
    USER_LOSE_ACTION("————！！操作失败！！————", 4048),
    USER_NOT_LOGIN("——！请先登录！——", 4010),
    USER_NOT_AUTH( "——！用户未授权！——", 4012),
    NOT_PARAMS("——！参数不存在！——", 4020),
    PARAMS_ERROR("——！参数不合法！——", 4024),
    TOO_MANY_TIMES_MESSAGE("——！请求超时！——", 5002),
    SYSTEM_ERROR("————！系统内部异常！————", 5000);

    private final int code;

    private final String msg;

    ErrorCodeEnum(String msg, int code) {
        this.code = code;
        this.msg = msg;
    }

}