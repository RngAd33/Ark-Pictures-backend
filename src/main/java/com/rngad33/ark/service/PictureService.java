package com.rngad33.ark.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.ark.model.dto.picture.PictureQueryRequest;
import com.rngad33.ark.model.dto.picture.PictureReviewRequest;
import com.rngad33.ark.model.dto.picture.PictureUploadByBatchRequest;
import com.rngad33.ark.model.dto.picture.PictureUploadRequest;
import com.rngad33.ark.model.entity.Picture;
import com.rngad33.ark.model.entity.User;
import com.rngad33.ark.model.vo.PictureVO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * 图片服务接口
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片上传
     *
     * @param inputSource 文件输入源（文件 / url）
     * @return 访问地址
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

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

    /**
     * 抓取图片批量上传（仅管理员）
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) throws IOException;

    /**
     * 清理图片
     *
     * @param oldPicture
     */
    void deletePicture(Picture oldPicture);

}