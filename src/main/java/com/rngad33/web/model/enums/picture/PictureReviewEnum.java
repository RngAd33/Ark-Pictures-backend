package com.rngad33.web.model.enums.picture;

import cn.hutool.core.util.ObjUtil;
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

    /**
     * 根据code获取枚举
     * @param code 审核状态值
     * @return 状态枚举
     */
    public static PictureReviewEnum getEnumByValue(Integer code) {
        if (ObjUtil.isEmpty(code)) {
            return null;
        }
        for (PictureReviewEnum pictureReviewEnum : PictureReviewEnum.values()) {
            if (pictureReviewEnum.code.equals(code)) {
                return pictureReviewEnum;
            }
        }
        return null;
    }

}