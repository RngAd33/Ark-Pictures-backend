package com.rngad33.web.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户更新请求体
 */
@Data
public class UserUpdateRequest implements Serializable {

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
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态：0-正常，1-封禁
     */
    private Integer userStatus;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 3191241716373120793L;

}