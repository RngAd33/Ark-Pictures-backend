package com.yupi.usercenter.exception;

import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.enums.ErrorCodeEnum;
import com.yupi.usercenter.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MyException.class)
    public BaseResponse myExceptionHandler(MyException e) {
        log.error("————！！MyException: {}！！————", e.getMsg(), e);
        return ResultUtils.error(e.getCode(), e.getMsg());
    }

    /**
     * 捕获Runtime异常（重要！防止系统异常信息泄露到前端）
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("————！！RuntimeException！！————", e);
        return ResultUtils.error(ErrorCodeEnum.SYSTEM_ERROR);
    }
}