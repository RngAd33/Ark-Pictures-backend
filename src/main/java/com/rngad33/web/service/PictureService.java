package com.rngad33.web.service;

import com.rngad33.web.model.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.web.model.dto.picture.PictureUploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务接口
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    String uploadPicture(String fileName, String filePath, MultipartFile multipartFile);

    /**
     * 图片上传（附带信息）
     *
     * @param multipartFile
     * @param uploadPathPrefix
     * @return
     */
    PictureUploadResult uploadPictureWithInfo(MultipartFile multipartFile, String uploadPathPrefix);

}