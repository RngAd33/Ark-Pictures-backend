package com.rngad33.web.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户更新请求体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 身份？ 0-普通用户，1-管理员
     */
    private Integer role;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 用户状态：0-正常，1-封禁
     */
    private Integer userStatus;

    /**
     * 更新时间
     */
    private Date updateTime;

}