package com.rngad33.web.model.dto.picture;

import java.io.Serializable;

/**
 * 文件下载请求体
 */
public class PictureDownloadRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String fileName;

    private String filePath;

}