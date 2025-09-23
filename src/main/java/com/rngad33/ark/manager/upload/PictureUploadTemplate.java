package com.rngad33.ark.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.rngad33.ark.config.CosClientConfig;
import com.rngad33.ark.constant.ErrorConstant;
import com.rngad33.ark.exception.MyException;
import com.rngad33.ark.manager.CosManager;
import com.rngad33.ark.model.dto.file.PictureUploadResult;
import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 图片上传模板抽象类
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    protected CosManager cosManager;

    @Resource
    protected CosClientConfig cosClientConfig;

    /**
     * 定义上传流程
     *
     * @param inputSource 文件或url
     * @param uploadPathPrefix 路径前缀
     * @return 上传结果
     */
    public final PictureUploadResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验图片
        validPicture(inputSource);
        // 约定图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFileName = getOriginFilename(inputSource);
        String suffix = FileUtil.getSuffix(originalFileName);
        // 临时对冲抓取图片扩展名异常问题
        if (StrUtil.length(suffix) > 5) {
            suffix = "jpg";
        }
        // 校验后缀是否存在
        if (StrUtil.isBlank(suffix)) {
            throw new MyException(ErrorCodeEnum.NOT_PARAMS, "文件后缀为空！");
        }
        // 自己拼接图片上传路径，不使用原始名称，保证安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, suffix);
        String uploadFilePath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;
        try {
            // 创建临时文件，获取文件到服务器
            file = File.createTempFile(uploadFilePath, null);
            // 处理文件来源
            processFile(inputSource, file);
            // 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadFilePath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取图片处理结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                // 获取压缩后的图片信息
                CIObject compressCiObject = objectList.get(0);
                // - 缩略图默认等同于压缩图
                CIObject thumbnailCiObject = compressCiObject;
                if (file.length() > 1024 * 2) {
                    thumbnailCiObject = objectList.get(1);
                }
                return this.buildResult(originalFileName, compressCiObject, thumbnailCiObject);
            }
            // 封装原图的返回结果
            return this.buildResult(originalFileName, file, imageInfo, uploadFilePath);
        } catch (Exception e) {
            log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE + uploadFilePath, e);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        } finally {
            // 清理临时文件
            this.deleteTempFile(file);
        }
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
     * 封装原图返回结果
     *
     * @param originalFileName 原始文件名
     * @param file 临时文件
     * @param imageInfo 对象存储返回的图片信息
     * @param uploadFilePath 上传路径
     * @return 可访问地址
     */
    private PictureUploadResult buildResult(String originalFileName, File file, ImageInfo imageInfo, String uploadFilePath) {
        PictureUploadResult pictureUploadResult = new PictureUploadResult();
        // 计算宽高
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();
        double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
        // 封装返回结果
        pictureUploadResult.setOriginUrl(cosClientConfig.getHost() + "/" + uploadFilePath);
        pictureUploadResult.setPicName(FileUtil.mainName(originalFileName));
        pictureUploadResult.setPicSize(FileUtil.size(file));
        pictureUploadResult.setPicWidth(width);
        pictureUploadResult.setPicHeight(height);
        pictureUploadResult.setPicScale(scale);
        pictureUploadResult.setPicFormat(imageInfo.getFormat());
        return pictureUploadResult;
    }

    /**
     * 封装压缩图返回结果
     *
     * @param originalFileName 原始文件名
     * @param compressCiObject 压缩后的对象
     * @param thumbnailCiObject 缩略图对象
     * @return 可访问地址
     */
    private PictureUploadResult buildResult(String originalFileName, CIObject compressCiObject, CIObject thumbnailCiObject) {
        // 计算宽高
        int width = compressCiObject.getWidth();
        int height = compressCiObject.getHeight();
        double scale = NumberUtil.round(width * 1.0 / height, 2).doubleValue();
        // 封装返回结果
        PictureUploadResult pictureUploadResult = new PictureUploadResult();
        pictureUploadResult.setOriginUrl(cosClientConfig.getHost() + "/" + compressCiObject.getKey());
        pictureUploadResult.setThumbUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        pictureUploadResult.setPicName(FileUtil.mainName(originalFileName));
        pictureUploadResult.setPicSize(compressCiObject.getSize().longValue());
        pictureUploadResult.setPicWidth(width);
        pictureUploadResult.setPicHeight(height);
        pictureUploadResult.setPicScale(scale);
        pictureUploadResult.setPicFormat(compressCiObject.getFormat());
        // 缩略图地址
        pictureUploadResult.setThumbUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        return pictureUploadResult;
    }

    /**
     * 清理临时文件
     *
     * @param file 临时文件
     */
    private void deleteTempFile(File file) {
        if (file != null) {
            return;
        }
        boolean del = file.delete();
        if (!del) {
            log.error("file delete fail: " + file.getAbsolutePath());
        }
    }

}