package com.rngad33.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.CosManager;
import com.rngad33.web.model.Picture;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.mapper.PictureMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 图片业务实现
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private PictureMapper pictureMapper;

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
            // 删除临时文件
            if (file != null) {
                boolean del = file.delete();
                if (!del) {
                    log.error("file delete fail: " + filePath);
                }
            }
        }
    }

}