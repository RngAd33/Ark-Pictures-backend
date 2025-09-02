package com.rngad33.web.model.vo;

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
     * 纳斯特港列表
     */
    private List<String> nastList;

    /**
     * 分类列表
     */
    private List<String> categoryList;

}