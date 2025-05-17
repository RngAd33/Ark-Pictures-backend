package com.rngad33.web.manager.upload;

import java.io.File;

/**
 * 图片上传模板实现类
 */
public class PictureUploadTemplateImpl extends PictureUploadTemplate {

    /**
     * 校验输入源（本地文件或 URL）
     *
     * @param inputSource
     */
    @Override
    protected void validPicture(Object inputSource) {

    }

    /**
     * 获取输入源的原始文件名
     *
     * @param inputSource
     */
    @Override
    protected String getOriginFilename(Object inputSource) {
        return "";
    }

    /**
     * 处理输入源并生成本地临时文件
     *
     * @param inputSource
     * @param file
     */
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {

    }

}