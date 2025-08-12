package com.rngad33.ark.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求体
 */
@Data
public class PictureUploadRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String name;

    private static final long serialVersionUID = 3191241716373120793L;

}