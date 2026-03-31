package com.rngad33.ark.model.dto.picture;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 鏍囩鍒嗙被鍒楄〃瑙嗗浘
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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