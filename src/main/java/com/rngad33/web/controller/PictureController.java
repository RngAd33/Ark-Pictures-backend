package com.rngad33.web.controller;

import com.qcloud.cos.model.COSObject;
import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.enums.ErrorCodeEnum;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.CosManager;
import com.rngad33.web.model.request.PictureUploadRequest;
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
 * 图片交互接口
 */
@Slf4j
@RestController
@RequestMapping("/pic")
public class PictureController {

    @Resource
    private PictureService pictureService;

    @Resource
    private CosManager cosManager;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @return
     * @throws IOException
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<String> uploadPicture(@RequestPart("/file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest) throws IOException {
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
        } catch (IOException e) {
            log.error("file upload fail: " + filePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            if (file != null) {
                boolean del = file.delete();
                if (!del) {
                    log.error("file delete fail: " + filePath);
                }
            }
        }
    }

    /**
     * 文件下载
     *
     * @param filePath
     * @param response
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/download")
    public void downloadPicture(String filePath, HttpServletResponse response) {
        // 文件对象
        COSObject cosObject = cosManager.getObject(filePath);
    }

}