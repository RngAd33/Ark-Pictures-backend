package com.rngad33.ark.model.dto.thumb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 点赞请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThumbRequest {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 图片 id
     */
    private Long pictureId;

}