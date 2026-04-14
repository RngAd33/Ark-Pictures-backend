package com.rngad33.ark.model.enums.misc;

import lombok.Getter;

/**
 * Lua 脚本执行状态枚举
 */
@Getter
public enum LuaStatusEnum {
    // 成功
    SUCCESS(1L),
    // 失败
    FAIL(-1L);

    private final long value;

    LuaStatusEnum(long value) {
        this.value = value;
    }

}