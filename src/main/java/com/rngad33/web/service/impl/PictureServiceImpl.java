package com.rngad33.web.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.rngad33.web.config.CosClientConfig;
import com.rngad33.web.constant.ErrorConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.CosManager;
import com.rngad33.web.manager.PictureManager;
import com.rngad33.web.model.Picture;
import com.rngad33.web.model.dto.picture.PictureUploadResult;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.mapper.PictureMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 图片业务实现
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private PictureManager pictureManager;

    @Resource
    private CosManager cosManager;

    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    @Override
    public String uploadPicture(String fileName, String filePath, MultipartFile multipartFile) {
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            // 返回可访问地址
            return filePath;
        } catch (Exception e) {
            log.error("file upload fail: " + filePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 清理临时文件
            pictureManager.deleteTempFile(file);
        }
    }

    /**
     * 图片上传（附带信息）
     *
     * @param multipartFile
     * @param uploadPathPrefix
     * @return
     */
    public PictureUploadResult uploadPictureWithInfo(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验图片
        pictureManager.validPicture(multipartFile);
        // 约定图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFileName = uploadPathPrefix + multipartFile.getOriginalFilename() + uuid;
        // 自己拼接图片上传路径，不使用原始名称，保证安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.format(new DateTime(), "yyyyMMddHHmmss"), uuid,
                FileUtil.getSuffix(originalFileName));
        String uploadFilePath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadFilePath, null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putObject(uploadFilePath, file);
            // 获取图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 封装返回结果
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
            PictureUploadResult pictureUploadResult = new PictureUploadResult();
            pictureUploadResult.setUrl(cosClientConfig.getHost() + File.separator + uploadFilePath);
            pictureUploadResult.setPicName(FileUtil.mainName(originalFileName));
            pictureUploadResult.setPicSize(FileUtil.size(file));
            pictureUploadResult.setPicWidth(imageInfo.getWidth());
            pictureUploadResult.setPicHeight(imageInfo.getHeight());
            pictureUploadResult.setPicScale(scale);
            pictureUploadResult.setPicFormat(imageInfo.getFormat());
            // 返回可访问地址
            return pictureUploadResult;
        } catch (Exception e) {
            log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE + uploadFilePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 清理临时文件
            pictureManager.deleteTempFile(file);
        }
    }

}