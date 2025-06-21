package com.rngad33.web.model.dto.user;

import lombok.Data;

/**
 * 用户查询请求体
 */
@Data
public class UserQueryRequest {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userName;

}