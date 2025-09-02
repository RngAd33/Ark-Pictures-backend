package com.rngad33.web.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rngad33.web.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求体
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserQueryRequest extends PageRequest {

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
     * 电话
     */
    private String phone;

    /**
     * 用户状态：0-正常，1-封禁
     */
    private Integer userStatus;

}