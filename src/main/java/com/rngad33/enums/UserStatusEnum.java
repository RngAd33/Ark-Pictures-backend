package com.yupi.usercenter.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatusEnum {

    NORMAL_STATUS("正常", 0),
    BAN_STATUS("封禁", 1);

    private final String status;

    private final Integer value;

    UserStatusEnum(String status, Integer value) {
        this.status = status;
        this.value = value;
    }

}