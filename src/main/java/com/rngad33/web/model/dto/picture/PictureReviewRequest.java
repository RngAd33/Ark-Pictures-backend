package com.rngad33.web.model.dto.picture;

import lombok.Data;

/**
 * 图片审核请求体
 */
@Data
public class PictureReviewRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 3191241716373120793L;

}