package com.rngad33.web.manager;

import com.rngad33.web.constant.AESConstant;
import com.rngad33.web.constant.ErrorConstant;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.model.dto.user.UserManageRequest;
import com.rngad33.web.model.entity.Picture;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.enums.picture.PictureReviewStatusEnum;
import com.rngad33.web.model.enums.user.UserRoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 通用用户操作
 */
@Service
public class UserManager {

    /**
     * 正向鉴权（面向请求）
     *
     * @param request http请求
     * @return 是否（TF）为管理员
     */
    @Deprecated
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || !Objects.equals(user.getRole(), UserRoleEnum.ADMIN_ROLE.getCode())) {
            System.out.println(ErrorConstant.USER_NOT_AUTH_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 正向鉴权（面向用户）
     *
     * @param user 用户
     * @return 是否（TF）为管理员
     */
    @Deprecated
    public boolean isAdmin(User user) {
        if (user == null || !Objects.equals(user.getRole(), UserRoleEnum.ADMIN_ROLE.getCode())) {
            System.out.println(ErrorConstant.USER_NOT_AUTH_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 反向鉴权（面向请求）
     *
     * @param request http请求
     * @return 是否（TF）为管理员
     */
    @Deprecated
    public boolean isNotAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || !Objects.equals(user.getRole(), UserRoleEnum.ADMIN_ROLE.getCode())) {
            System.out.println(ErrorConstant.USER_NOT_AUTH_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 反向鉴权（面向用户）
     *
     * @param user 用户
     * @return 是否（TF）为管理员
     */
    @Deprecated
    public boolean isNotAdmin(User user) {
        if (user == null || !Objects.equals(user.getRole(), UserRoleEnum.ADMIN_ROLE.getCode())) {
            System.out.println(ErrorConstant.USER_NOT_AUTH_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * 用户脱敏
     * 使用掩码隐藏敏感信息，保障传输层安全
     * @param user 脱敏前的账户
     * @return 脱敏后的账户
     */
    public User getSafeUser(User user) {
        if (user == null) return null;
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setPlanetCode(user.getPlanetCode());
        safeUser.setRole(user.getRole());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setUserPassword(AESConstant.CONFUSION);
        safeUser.setPhone(AESConstant.CONFUSION);
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(new Date());
        return safeUser;
    }

    /**
     * 传递id
     *
     * @param userManageRequest 用户管理请求体
     * @param request 用户登录态
     * @return id
     */
    public Long getId(UserManageRequest userManageRequest, HttpServletRequest request) {
        if (isNotAdmin(request)) return null;   // 鉴权，仅管理员可操作
        if (userManageRequest == null) return null;   // 验证请求体
        Long id = userManageRequest.getId();
        if (id <= 0) return null;
        return id;
    }

    /**
     * 补充审核参数
     *
     * @param picture
     * @param loginUser
     */
    public void fillReviewParams(Picture picture, User loginUser) {
        if (isNotAdmin(loginUser)) {
            // 非管理员创建和编辑都要重新审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getCode());
        } else {
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getCode());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewMessage("管理员自动过审>>>");
        }
    }

}