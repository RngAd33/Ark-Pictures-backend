package com.rngad33.web.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 通用文件操作（已废弃）
 */
@Slf4j
@Service
@Deprecated
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
     * 通过url上传图片
     *
     * @param fileUrl 文件url
     * @param uploadPathPrefix 路径前缀
     * @return
     */
    public PictureUploadResult uploadPictureByUrl(String fileUrl, String uploadPathPrefix) {
        // 校验图片
        validPicture(fileUrl);
        // 约定图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFileName = fileUrl;
        // 自己拼接图片上传路径，不使用原始名称，保证安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFileName));
        String uploadFilePath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadFilePath, null);
            // 下载文件
            HttpUtil.downloadFile(fileUrl, file);
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
        ThrowUtils.throwIf(multipartFile.getSize() > 1024 * 1024 * 10,
                ErrorCodeEnum.PARAM_ERROR, "图片大小不能超过10M！");
        // - 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> SUFFIX_ALLOW = Arrays.asList("png", "jpg", "jpeg", "gif", "webp");
        ThrowUtils.throwIf(!SUFFIX_ALLOW.contains(fileSuffix), ErrorCodeEnum.PARAM_ERROR, "不支持的图片格式！");
    }

    /**
     * 文件校验
     *
     * @param fileUrl 文件url
     */
    public void validPicture(String fileUrl) {
        // 校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCodeEnum.PARAM_ERROR, "文件url不能为空！");
        // 校验url格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new MyException("url格式不正确！", ErrorCodeEnum.PARAM_ERROR);
        }
        // 校验url协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
                ErrorCodeEnum.PARAM_ERROR, "仅支持HTTP或HTTPS协议！");

        HttpResponse response = null;
        try {
            // 发送HEAD请求，验证文件是否存在
            response = HttpUtil.createRequest(Method.HEAD, fileUrl)
                    .execute();
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                // 未正常返回，无需执行其它判断
                return;
            }
            // 文件存在，校验文件类型
            String contentType = response.header("Content-Type");
            // 结果不为空才校验
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCodeEnum.PARAM_ERROR, "文件类型错误！");
            }
            // 文件存在，校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long LIMIT = 10 * 1024 * 1024L;   // 限制文件大小
                    ThrowUtils.throwIf(contentLength > LIMIT, ErrorCodeEnum.PARAM_ERROR, "文件大小不能超过10M");
                } catch (NumberFormatException e) {
                    throw new MyException("文件大小错误！", ErrorCodeEnum.PARAM_ERROR);
                }
            }
        } finally {
            // 释放资源
            if (response != null) {
                response.close();
            }
        }
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