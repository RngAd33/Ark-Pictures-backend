package com.rngad33.web.service;

import com.rngad33.web.model.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.web.model.User;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务接口
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片上传
     *
     * @param multipartFile 原始文件
     * @return 访问地址
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

}