package com.rngad33.web.model.dto.file;

import lombok.Data;

/**
 * 文件上传结果对象
 */
@Data
public class PictureUploadResult {

    /**
     * 地址
     */
    private String url;

    /**
     * 名称
     */
    private String picName;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 宽度
     */
    private int picWidth;

    /**
     * 高度
     */
    private int picHeight;

    /**
     * 宽高比
     */
    private Double picScale;

    /**
     * 格式
     */
    private String picFormat;

}
