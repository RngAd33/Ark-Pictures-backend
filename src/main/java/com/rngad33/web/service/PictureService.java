package com.rngad33.web.service;

import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.model.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.web.model.request.PictureUploadRequest;

/**
 * 图片服务接口
 */
public interface PictureService extends IService<Picture> {

    BaseResponse<String> uploadPicture(PictureUploadRequest pictureUploadRequest);

}