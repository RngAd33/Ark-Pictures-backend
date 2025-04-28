package com.yupi.usercenter.enums;

import lombok.Getter;

/**
 * 用户身份枚举
 */
@Getter
public enum UserRoleEnum {

    USER_LOGIN_STATE("用户态键", "userLoginState"),
    ADMIN_ROLE("管理员", 1),
    DEFAULT_ROLE("普通用户", 0);

    private final String name;
    private final String key;

    private final String role;
    private final Integer code;

    UserRoleEnum(String name, String key) {
        this.name = name;
        this.key = key;
        this.role = null;
        this.code = null;
    }

    UserRoleEnum(String role, Integer code) {
        this.name = null;
        this.key = null;
        this.role = role;
        this.code = code;
    }

}