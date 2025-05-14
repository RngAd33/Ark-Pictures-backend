package com.rngad33.web.model.enums.picture;

import lombok.Getter;

/**
 * 审核状态枚举类
 */
@Getter
public enum PictureReviewEnum {

    REVIEWING("审核中", 0),
    PASS("已通过", 1),
    FAIL("已驳回", 2);

    private final String status;
    private final Integer code;

    PictureReviewEnum(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

}