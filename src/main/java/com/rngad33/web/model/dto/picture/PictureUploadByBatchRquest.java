package com.rngad33.web.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量导入图片请求
 */
@Data
public class PictureUploadByBatchRquest implements Serializable  {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    private static final long serialVersionUID = 3191241716373120793L;
}