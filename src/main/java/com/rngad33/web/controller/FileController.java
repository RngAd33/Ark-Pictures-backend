package com.rngad33.web.controller;

import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.service.FileService;
import com.rngad33.web.common.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件交互接口
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 文件上传
     *
     * @param multipartFile 原始文件
     * @return 可访问地址
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("/file") MultipartFile multipartFile) {
        try {
            String filePath = fileService.uploadFile(multipartFile);
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("file upload fail", e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }
    }

    /**
     * 文件下载
     *
     * @param filePath 文件路径
     * @param response HTTP响应
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/download")
    public void downloadFile(String filePath, HttpServletResponse response) {
        try {
            fileService.downloadFile(filePath, response);
        } catch (Exception e) {
            log.error("file download fail: {}", filePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }
    }

}