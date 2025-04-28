package com.yupi.usercenter.service;

import com.yupi.usercenter.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 业务接口层
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userName 用户名
     * @param userPassword 密码
     * @param checkPassword 确认密码
     * @param planetCode 星球编号
     * @return 新账户id
     */
    Long userRegister(String userName, String userPassword, String checkPassword, String planetCode) throws Exception;

    /**
     * 用户登录
     *
     * @param userName 账号
     * @param userPassword 密码
     * @return 脱敏后的登录态
     */
    User userLogin(String userName, String userPassword, HttpServletRequest request) throws Exception;

    /**
     * 获取当前用户状态
     *
     * @param request http请求
     * @return 登录态
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 退出登录
     *
     * @param request http请求
     * @return 状态码
     */
    Integer userLogout(HttpServletRequest request);

    /**
     * 用户查询（仅管理员）
     *
     * @param userName 用户名
     * @return 用户列表
     */
    List<User> searchUsers(String userName, HttpServletRequest request);

    /**
     * 用户封禁 / 解封（仅管理员）
     *
     * @param id 待封禁/解封用户id
     * @return 状态码
     */
    Integer userOrBan(Long id, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param id 待注销用户id
     * @return 注销结果
     */
    Integer userLogoff(Long id, HttpServletRequest request);

}