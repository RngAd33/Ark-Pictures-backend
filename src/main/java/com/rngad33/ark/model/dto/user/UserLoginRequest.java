package com.rngad33.ark.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 鐢ㄦ埛鐧诲綍璇锋眰浣撶？
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginRequest {

    private String userName, userPassword;

}