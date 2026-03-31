package com.rngad33.ark.model.dto.picture;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 图片编辑请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PictureEditRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

}