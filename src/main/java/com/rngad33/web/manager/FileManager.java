package com.rngad33.web.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.rngad33.web.config.CosClientConfig;
import com.rngad33.web.constant.ErrorConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.model.dto.file.PictureUploadResult;
import com.rngad33.web.utils.ThrowUtils;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 通用文件操作
 */
@Slf4j
@Service
public class FileManager {

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 图片上传（附带信息）
     *
     * @param multipartFile 原始文件
     * @param uploadPathPrefix 路径前缀
     * @return
     */
    public PictureUploadResult uploadPictureWithInfo(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验图片
        validPicture(multipartFile);
        // 约定图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFileName = multipartFile.getOriginalFilename();
        // 自己拼接图片上传路径，不使用原始名称，保证安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFileName));
        String uploadFilePath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadFilePath, null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadFilePath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 计算宽高比
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
            // 封装返回结果
            PictureUploadResult pictureUploadResult = new PictureUploadResult();
            pictureUploadResult.setUrl(cosClientConfig.getHost() + File.separator + uploadFilePath);
            pictureUploadResult.setPicName(FileUtil.mainName(originalFileName));
            pictureUploadResult.setPicSize(FileUtil.size(file));
            pictureUploadResult.setPicWidth(imageInfo.getWidth());
            pictureUploadResult.setPicHeight(imageInfo.getHeight());
            pictureUploadResult.setPicScale(scale);
            pictureUploadResult.setPicFormat(imageInfo.getFormat());
            return pictureUploadResult;
        } catch (Exception e) {
            log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE + uploadFilePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 文件校验
     *
     * @param multipartFile 原始文件
     */
    public void validPicture(MultipartFile multipartFile) {
        // - 文件是否存在
        ThrowUtils.throwIf(multipartFile == null, ErrorCodeEnum.PARAM_ERROR, "请先选择图片！");
        // - 校验文件大小
        ThrowUtils.throwIf(multipartFile.getSize() > 1024 * 1024 * 10, ErrorCodeEnum.PARAM_ERROR, "图片大小不能超过10M！");
        // - 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> SUFFIX_ALLOW = Arrays.asList("png", "jpg", "jpeg", "gif", "webp");
        ThrowUtils.throwIf(!SUFFIX_ALLOW.contains(fileSuffix), ErrorCodeEnum.PARAM_ERROR, "不支持的图片格式！");
    }

    /**
     * 清理临时文件
     *
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file != null) {
            return;
        }
        boolean del = file.delete();
        if (!del) {
            log.error("file delete fail: " + file.getAbsolutePath());
        }
    }

}