package com.rngad33.ark.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件服务接口
 */
@Deprecated
public interface FileService {

    /**
     * 文件上传
     *
     * @param multipartFile 上传的文件
     * @return 文件路径
     * @throws Exception 上传异常
     */
    String uploadFile(MultipartFile multipartFile) throws Exception;

    /**
     * 文件下载
     *
     * @param filePath 文件路径
     * @param response HTTP响应
     * @throws Exception 下载异常
     */
    void downloadFile(String filePath, HttpServletResponse response) throws Exception;

    /**
     * 清理临时文件
     *
     * @param file
     */
    void deleteTempFile(File file);

}