package com.rngad33.ark.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 用户注册请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegisterRequest {

    private String userName, userPassword, checkPassword, phone, planetCode;

}