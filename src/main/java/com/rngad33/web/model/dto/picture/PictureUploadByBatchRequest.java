package com.rngad33.web.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量导入图片请求体
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 图片名称前缀
     */
    private String namePrefix;

    private static final long serialVersionUID = 3191241716373120793L;

}