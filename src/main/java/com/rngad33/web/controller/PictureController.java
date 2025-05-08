package com.rngad33.web.controller;

import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.enums.UserRoleEnum;
import com.rngad33.web.model.request.PictureUploadRequest;
import com.rngad33.web.service.PictureService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图片交互接口
 */
@RestController
@RequestMapping("/pic")
public class PictureController {

    @Resource
    private PictureService pictureService;

    /**
     * 图片上传
     *
     * @param pictureUploadRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<String> uploadPicture(PictureUploadRequest pictureUploadRequest) {

        return pictureService.uploadPicture(pictureUploadRequest);
    }

}