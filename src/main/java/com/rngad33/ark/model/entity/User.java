package com.rngad33.ark.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * 用户模型
 *
 * @TableName user
 */
@Data
@Table(value = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    /**
     * 用户 id（主要服务于Mapper）
     * 当前策略为主键自增
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户昵称
     */
    @Column("user_name")
    private String userName;

    /**
     * 星球编号
     */
    @Column("planet_code")
    private String planetCode;

    /**
     * 身份？ 0-普通用户，1-管理员
     */
    private Integer role;

    /**
     * 头像地址
     */
    @Column("avatar_url")
    private String avatarUrl;

    /**
     * 密码
     */
    @Column("user_password")
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 用户状态：0-正常，1-封禁
     */
    @Column("user_status")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @Column("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    private Date updateTime;

    /**
     * 是否删除？ 0-未删，1-已删
     */
    @Column(value = "is_delete", isLogicDelete = true)
    private Integer isDelete;

}