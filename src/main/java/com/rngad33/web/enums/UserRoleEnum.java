package com.rngad33.web.enums;

import lombok.Getter;

/**
 * 用户身份枚举
 */
@Getter
public enum UserRoleEnum {

    ADMIN_ROLE("admin", 1),
    DEFAULT_ROLE("user", 0);

    private final String role;
    private final Integer code;

    UserRoleEnum(String role, Integer code) {
        this.role = role;
        this.code = code;
    }

}