package com.rngad33.web.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.rngad33.web.config.CosClientConfig;
import com.rngad33.web.constant.ErrorConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.CosManager;
import com.rngad33.web.model.dto.file.PictureUploadResult;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;

/**
 * 图片上传上传抽象类
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    protected CosManager cosManager;

    @Resource
    protected CosClientConfig cosClientConfig;

    /**
     * 模板方法：定义上传流程
     *
     * @param inputSource
     * @param uploadPathPrefix
     * @return
     */
    public final PictureUploadResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验图片
        validPicture(inputSource);
        // 约定图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFileName = getOriginFilename(inputSource);
        // 自己拼接图片上传路径，不使用原始名称，保证安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFileName));
        String uploadFilePath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadFilePath, null);
            // 处理文件来源
            processFile(inputSource, file);

            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadFilePath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            return buildResult(imageInfo, uploadFilePath, originalFileName, file);
        } catch (Exception e) {
            log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE + uploadFilePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 封装返回结果
     *
     * @param imageInfo
     * @param uploadFilePath
     * @param originalFileName
     * @param file
     * @return
     */
    private PictureUploadResult buildResult(ImageInfo imageInfo, String uploadFilePath, String originalFileName, File file) {
        PictureUploadResult pictureUploadResult = new PictureUploadResult();
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();
        double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
        pictureUploadResult = new PictureUploadResult();
        pictureUploadResult.setUrl(cosClientConfig.getHost() + File.separator + uploadFilePath);
        pictureUploadResult.setPicName(FileUtil.mainName(originalFileName));
        pictureUploadResult.setPicSize(FileUtil.size(file));
        pictureUploadResult.setPicWidth(imageInfo.getWidth());
        pictureUploadResult.setPicHeight(imageInfo.getHeight());
        pictureUploadResult.setPicScale(scale);
        pictureUploadResult.setPicFormat(imageInfo.getFormat());
        return pictureUploadResult;
    }

    /**
     * 校验输入源（本地文件或 URL）
     *
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     *
     * @param inputSource
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     *
     * @param inputSource
     * @param file
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

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