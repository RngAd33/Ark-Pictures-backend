package com.rngad33.web.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param multipartFile 上传的文件
     * @return 文件路径
     * @throws Exception 上传异常
     */
    String uploadFile(MultipartFile multipartFile) throws Exception;

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param response HTTP响应
     * @throws Exception 下载异常
     */
    void downloadFile(String filePath, HttpServletResponse response) throws Exception;

}