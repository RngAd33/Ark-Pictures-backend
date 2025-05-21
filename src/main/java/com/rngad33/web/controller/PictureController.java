package com.rngad33.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.common.DeleteRequest;
import com.rngad33.web.common.ResultUtils;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.UserManager;
import com.rngad33.web.model.dto.picture.*;
import com.rngad33.web.model.entity.Picture;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.model.enums.picture.PictureReviewStatusEnum;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.service.UserService;
import com.rngad33.web.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 图片交互接口
 */
@Slf4j
@RestController
@RequestMapping("/pic")
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private UserManager userManager;

    @Resource
    private PictureService pictureService;

    /**
     * 图片上传（基于文件）
     *
     * @param multipartFile 当前文件
     * @return 访问地址
     */
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("pic") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 图片上传（基于url）
     *
     * @return 访问地址
     */
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPicture(@RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 图片编辑
     *
     * @param pictureEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,
            HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // 判断原图是否存在
        Long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAM, "原图不存在！");
        // 仅本人或管理员可编辑
        User loginUser = userService.getCurrentUser(request);
        if (!oldPicture.getUserId().equals(loginUser.getId()) && userManager.isNotAdmin(loginUser)) {
            throw new MyException(ErrorCodeEnum.USER_NOT_AUTH);
        }
        // 由实体类转换为DTO
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureEditRequest, picture);
        // 将list转换为json字符串
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 补充审核参数
        userManager.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION);
        return ResultUtils.success(true);
    }

    /**
     * 图片删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        Long id = deleteRequest.getId();
        User loginUser = userService.getCurrentUser(request);
        // 判断原图是否存在
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAM, "原图不存在！");
        // 仅本人或管理员可删除
        if (!oldPicture.getUserId().equals(loginUser.getId()) && userManager.isNotAdmin(loginUser)) {
            throw new MyException(ErrorCodeEnum.USER_NOT_AUTH);
        }
        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION);
        return ResultUtils.success(true);
    }

    /**
     * 图片更新（仅管理员）
     *
     * @param pictureUpdateRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // 判断原图是否存在
        Long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAM);
        // 由实体类转换为DTO
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureUpdateRequest, picture);
        // 将list转换为json字符串
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 补充审核参数
        User loginUser = userService.getCurrentUser(request);
        userManager.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION);
        return ResultUtils.success(true);
    }

    /**
     * 根据id获取图片（管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<Picture> getPictureById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCodeEnum.PARAM_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCodeEnum.NOT_PARAM);
        // 获取封装类
        return ResultUtils.success(picture);
    }

    /**
     * 根据id获取图片（用户）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCodeEnum.PARAM_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCodeEnum.NOT_PARAM);
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * 分页获取图片列表（管理员）
     *
     * @param pictureQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表（用户）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 13, ErrorCodeEnum.PARAM_ERROR);
        // 普通用户默认只能看到已过审的图片
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getCode());
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * 获取预置标签分类
     *
     * @return
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("每日推荐", "明日方舟", "原神", "碧蓝航线", "东方Project", "Bilibili", "风景", "Volcaloid", "misc");
        List<String> categoryList = Arrays.asList("电脑壁纸", "手机壁纸", "名梗弔图", "表情包", "头像系列", "MISC");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 图片审核（仅管理员）
     *
     * @param pictureReviewRequest
     * @param request
     * @return
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewPicture(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCodeEnum.PARAM_ERROR);
        User loginUser = userService.getCurrentUser(request);
        pictureService.reviewPicture(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }

}