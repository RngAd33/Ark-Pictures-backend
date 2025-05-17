package com.rngad33.web.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 图片上传模板实现类（基于文件）
 */
@Service
public class PictureUploadTemplateImplByFile extends PictureUploadTemplate {

    /**
     * 校验输入源（本地文件或 URL）
     *
     * @param inputSource
     */
    @Override
    protected void validPicture(Object inputSource) {
        // - 强制类型转换
        MultipartFile multipartFile = (MultipartFile) inputSource;
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
     * 获取输入源的原始文件名
     *
     * @param inputSource
     * @return 原始文件名
     */
    @Override
    protected String getOriginFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    /**
     * 处理输入源并生成本地临时文件
     *
     * @param inputSource
     * @param file
     */
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }

}