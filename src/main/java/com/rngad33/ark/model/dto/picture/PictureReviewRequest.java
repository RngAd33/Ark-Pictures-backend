package com.rngad33.ark.model.dto.picture;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 图片审核请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

}