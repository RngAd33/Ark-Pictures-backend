package com.rngad33.web.model.enums.picture;

import lombok.Getter;

/**
 * 审核状态枚举类
 */
@Getter
public enum PictureReviewEnum {

    VIEWING("审核中", 0),
    PASS("已过审", 1),
    FAIL("未过审", 2);

    private final String status;
    private final Integer code;

    PictureReviewEnum(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

}