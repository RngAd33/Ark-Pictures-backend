package com.rngad33.web.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.manager.FileManager;
import com.rngad33.web.model.dto.picture.PictureUploadResult;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.CosManager;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件交互接口
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    @Resource
    private FileManager fileManager;

    /**
     * 文件上传
     *
     * @param multipartFile 原始文件
     * @return 可访问地址
     * @throws IOException
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<String> uploadPicture(@RequestPart("/file") MultipartFile multipartFile) {
        // 文件目录
        String fileName = multipartFile.getName();
        String filePath = String.format("/test/%s", fileName);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            // 返回可访问地址
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("file upload fail: " + filePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 清理临时文件
            fileManager.deleteTempFile(file);
        }
    }

    /**
     * 图片下载
     *
     * @param filePath
     * @param response
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/download")
    public void downloadPicture(String filePath, HttpServletResponse response) throws Exception {
        COSObjectInputStream cosObjectInput = null;
        try {
            // 文件下载
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download fail: " + filePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 释放流
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}