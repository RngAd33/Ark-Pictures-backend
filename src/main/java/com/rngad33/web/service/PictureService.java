package com.rngad33.web.service;

import com.rngad33.web.model.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
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

}