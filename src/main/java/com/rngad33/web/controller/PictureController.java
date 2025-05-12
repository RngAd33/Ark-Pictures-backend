package com.rngad33.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.rngad33.web.annotation.AuthCheck;
import com.rngad33.web.common.BaseResponse;
import com.rngad33.web.common.DeleteRequest;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.UserManager;
import com.rngad33.web.model.dto.picture.PictureUpdateRequest;
import com.rngad33.web.model.entity.Picture;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.dto.picture.PictureUploadRequest;
import com.rngad33.web.model.enums.ErrorCodeEnum;
import com.rngad33.web.model.vo.PictureVO;
import com.rngad33.web.service.PictureService;
import com.rngad33.web.service.UserService;
import com.rngad33.web.common.ResultUtils;
import com.rngad33.web.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 图片上传
     *
     * @param multipartFile 原始文件
     * @return 访问地址
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("/pic") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
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
        ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAM);
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
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // 由实体类转换为DTO
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureUpdateRequest, picture);
        // 将list转换为json字符串
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 判断原图是否存在
        Long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCodeEnum.NOT_PARAM);
        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCodeEnum.USER_LOSE_ACTION);
        return ResultUtils.success(true);
    }

}