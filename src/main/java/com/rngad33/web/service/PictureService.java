package com.rngad33.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rngad33.web.model.dto.picture.PictureQueryRequest;
import com.rngad33.web.model.dto.picture.PictureReviewRequest;
import com.rngad33.web.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.vo.PictureVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务接口
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片上传
     *
     * @param multipartFile 原始文件
     * @return 访问地址
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 校验图片信息
     *
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 获取单个图片封装
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片审核（仅管理员）
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void reviewPicture(PictureReviewRequest pictureReviewRequest, User loginUser);

}