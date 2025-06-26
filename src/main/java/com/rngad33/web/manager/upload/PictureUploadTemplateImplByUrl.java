package com.rngad33.web.manager.upload;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.utils.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 图片上传模板实现类（基于URL）
 */
@Service
public class PictureUploadTemplateImplByUrl extends PictureUploadTemplate {

    /**
     * 校验输入源
     *
     * @param inputSource
     */
    @Override
    protected void validPicture(Object inputSource) {
        // 强制类型转换
        String fileUrl = (String) inputSource;
        // 校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCodeEnum.PARAMS_ERROR, "文件url不能为空！");
        // 校验url格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new MyException(ErrorCodeEnum.PARAMS_ERROR, "url格式不正确！");
        }
        // 校验url协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
                ErrorCodeEnum.PARAMS_ERROR, "仅支持HTTP或HTTPS协议链接！");
        // 发送HEAD请求，验证文件是否存在
        HttpResponse response = null;
        try {
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
                        ErrorCodeEnum.PARAMS_ERROR, "文件类型错误！");
            }
            // 文件存在，校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long LIMIT = 10 * 1024 * 1024L;   // 限制文件大小
                    ThrowUtils.throwIf(contentLength > LIMIT, ErrorCodeEnum.PARAMS_ERROR, "文件大小不能超过10M");
                } catch (NumberFormatException e) {
                    throw new MyException(ErrorCodeEnum.PARAMS_ERROR, "文件大小错误！");
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
     * 获取输入源的原始文件名
     *
     * @param inputSource
     * @return 原始文件名
     */
    @Override
    protected String getOriginFilename(Object inputSource) {
        return (String) inputSource;
    }

    /**
     * 处理输入源并生成本地临时文件
     *
     * @param inputSource
     * @param file
     */
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        // 下载文件到临时目录
        HttpUtil.downloadFile(fileUrl, file);
    }

}