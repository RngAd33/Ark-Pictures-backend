package com.rngad33.web.model.dto.picture;

import lombok.Data;

import java.util.List;

/**
 * 标签分类列表视图
 */
@Data
public class PictureTagCategory {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;

}