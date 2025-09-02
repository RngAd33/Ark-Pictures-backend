package com.rngad33.web.model.dto.picture;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PictureUploadRequest {

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

}