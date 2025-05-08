package com.rngad33.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.model.Picture;
import com.rngad33.web.model.request.PictureUploadRequest;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.mapper.PictureMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 图片业务实现
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private PictureMapper pictureMapper;

    /**
     * 图片上传
     *
     * @param pictureUploadRequest
     * @return
     */
    @Override
    public BaseResponse<String> uploadPicture(PictureUploadRequest pictureUploadRequest) {
        return null;
    }

}