package com.rngad33.web.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求体
 */
@Data
public class UserQueryRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userName;

}