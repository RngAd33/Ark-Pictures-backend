package com.rngad33.ark.controller;

import cn.hutool.core.util.ObjUtil;
import com.rngad33.ark.common.BaseResponse;
import com.rngad33.ark.model.dto.thumb.ThumbRequest;
import com.rngad33.ark.model.entity.User;
import com.rngad33.ark.model.enums.misc.ErrorCodeEnum;
import com.rngad33.ark.service.ThumbService;
import com.rngad33.ark.service.UserService;
import com.rngad33.ark.utils.ResultUtils;
import com.rngad33.ark.utils.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞接口
 */
@RestController
@RequestMapping("/thumb")
public class ThumbController {

    @Resource
    private ThumbService thumbService;

    /**
     * 点赞
     *
     * @param thumbRequest
     * @param request
     * @return
     */
    @PostMapping("/do")
    public BaseResponse<Boolean> doThumb(@RequestBody ThumbRequest thumbRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.hasNull(thumbRequest, request), ErrorCodeEnum.NOT_PARAMS);
        return ResultUtils.success(thumbService.doThumb(thumbRequest));
    }

    /**
     * 取消点赞
     *
     * @param thumbRequest
     * @param request
     * @return
     */
    @PostMapping("/undo")
    public BaseResponse<Boolean> unThumb(@RequestBody ThumbRequest thumbRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjUtil.hasNull(thumbRequest, request), ErrorCodeEnum.NOT_PARAMS);
        return ResultUtils.success(thumbService.unThumb(thumbRequest));
    }


}