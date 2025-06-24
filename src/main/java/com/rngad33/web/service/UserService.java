package com.rngad33.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rngad33.web.model.dto.user.UserAddRequest;
import com.rngad33.web.model.dto.user.UserQueryRequest;
import com.rngad33.web.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rngad33.web.model.vo.UserVO;
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
     * @param phone 手机号
     * @param planetCode 星球编号
     * @return 新账户id
     */
    Long userRegister(String userName, String userPassword, String checkPassword, String phone, String planetCode) throws Exception;

    /**
     * 用户登录
     *
     * @param userName 用户名
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
     * 用户模糊查询
     *
     * @param userName 用户名
     * @return 用户列表
     */
    List<User> searchUsers(String userName, HttpServletRequest request);

    /**
     * 分页查询对象构建
     *
     * @param userQueryRequest 用户查询请求对象
     * @return QueryWrapper 查询条件构造器
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 添加用户（仅管理员）
     *
     * @param userAddRequest
     * @return
     * @throws Exception
     */
    Long addUser(UserAddRequest userAddRequest) throws Exception ;

    /**
     * 用户封禁 / 解封（仅管理员）
     *
     * @param id 待封禁/解封用户id
     * @return 状态码
     */
    Integer userOrBan(Long id, HttpServletRequest request);

    /**
     * 获取单个用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

}