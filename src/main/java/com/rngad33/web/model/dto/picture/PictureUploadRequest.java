package com.rngad33.web.model.dto.picture;

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

    private static final long serialVersionUID = 3191241716373120793L;

}