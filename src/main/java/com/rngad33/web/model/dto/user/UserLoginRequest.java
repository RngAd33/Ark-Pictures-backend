package com.rngad33.web.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginRequest {

    private String userName, userPassword;

}