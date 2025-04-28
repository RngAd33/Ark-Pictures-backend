package com.yupi.usercenter.model.request;

import lombok.Data;
import java.io.Serializable;

/**
 * 用户管理请求体
 */
@Data
public class UserManageRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private Long id;

}