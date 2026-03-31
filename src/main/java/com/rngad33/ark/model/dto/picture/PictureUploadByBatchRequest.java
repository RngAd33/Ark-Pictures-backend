package com.rngad33.ark.model.dto.picture;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 图片抓取请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PictureUploadByBatchRequest {

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

    /**
     * 目标图源
     */
    private String library;

}