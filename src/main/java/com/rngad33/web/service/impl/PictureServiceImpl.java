package com.rngad33.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.web.manager.FileManager;
import com.rngad33.web.model.Picture;
import com.rngad33.web.model.User;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.dto.file.PictureUploadResult;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.mapper.PictureMapper;
import com.rngad33.web.utils.ThrowUtils;
import java.util.Date;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片业务实现
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private FileManager fileManager;

    /**
     * 图片上传
     *
     * @param multipartFile 原始文件
     * @return 图片封装类
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 校验是否登录
        ThrowUtils.throwIf(loginUser == null, ErrorCodeEnum.USER_NOT_AUTH);
        // 判断是新增还是删除
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新，判断图片是否存在
        if (pictureId != null) {
            boolean exist = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exist, ErrorCodeEnum.NOT_PARAM);
        }
        // 上传图片，按照用户id划分目录
        String uploadFilePrefix = String.format("public/%s", loginUser.getId());
        PictureUploadResult pictureUploadResult = fileManager.uploadPictureWithInfo(multipartFile, uploadFilePrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(pictureUploadResult.getUrl());
        picture.setName(pictureUploadResult.getPicName());
        picture.setPicSize(pictureUploadResult.getPicSize());
        picture.setPicWidth(pictureUploadResult.getPicWidth());
        picture.setPicHeight(pictureUploadResult.getPicHeight());
        picture.setPicScale(pictureUploadResult.getPicScale());
        picture.setPicFormat(pictureUploadResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 操作数据库
        if (pictureId != null) {
            // 更新，补充信息
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION, "——！图片上传失败！——");
        return PictureVO.objToVo(picture);
    }

}