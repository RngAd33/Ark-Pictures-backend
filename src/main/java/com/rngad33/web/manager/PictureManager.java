package com.rngad33.web.manager;

import cn.hutool.core.io.FileUtil;
import com.rngad33.web.utils.ThrowUtils;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 图片操作
 */
@Slf4j
public class PictureManager {

    /**
     * 图片校验
     *
     * @param multipartFile
     */
    public void validPicture(MultipartFile multipartFile) {
        // - 文件是否存在
        ThrowUtils.throwIf(multipartFile == null, ErrorCodeEnum.PARAM_ERROR, "文件不能为空！");
        // - 校验文件大小
        ThrowUtils.throwIf(multipartFile.getSize() > 1024 * 1024 * 5, ErrorCodeEnum.PARAM_ERROR, "文件大小不能超过5M！");
        // - 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> SUFFIX_ALLOW = Arrays.asList("png", "jpg", "jpeg", "gif", "webp");
        ThrowUtils.throwIf(!SUFFIX_ALLOW.contains(fileSuffix), ErrorCodeEnum.PARAM_ERROR, "不支持的文件格式！");
    }

    /**
     * 删除临时文件
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