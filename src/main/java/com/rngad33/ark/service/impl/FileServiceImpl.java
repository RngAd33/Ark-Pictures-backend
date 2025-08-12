package com.rngad33.ark.service.impl;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.rngad33.ark.manager.CosManager;
import com.rngad33.ark.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件服务实现
 */
@Slf4j
@Service
@Deprecated
public class FileServiceImpl implements FileService {

    @Resource
    private CosManager cosManager;

    /**
     * 文件上传
     *
     * @param multipartFile 上传的文件
     * @return 文件路径
     * @throws IOException
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getName();
        String filePath = String.format("/public/%s", fileName);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            // 上传到COS
            cosManager.putObject(filePath, file);
            return filePath;
        } finally {
            // 清理临时文件
            this.deleteTempFile(file);
        }
    }

    /**
     * 文件下载
     *
     * @param filePath 文件路径
     * @param response HTTP响应
     * @throws IOException
     */
    @Override
    public void downloadFile(String filePath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            // 从COS获取文件
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } finally {
            // 释放流
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

    /**
     * 清理临时文件
     *
     * @param file
     */
    @Override
    public void deleteTempFile(File file) {
        if (file != null) {
            return;
        }
        boolean del = file.delete();
        if (!del) {
            log.error("file delete fail: " + file.getAbsolutePath());
        }
    }

}