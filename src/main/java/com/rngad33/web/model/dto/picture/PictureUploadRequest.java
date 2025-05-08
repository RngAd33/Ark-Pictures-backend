package com.rngad33.web.model.dto.picture;

import lombok.Data;
import java.io.Serializable;

/**
 * 文件上传请求体
 */
@Data
public class PictureUploadRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private Long id;

}