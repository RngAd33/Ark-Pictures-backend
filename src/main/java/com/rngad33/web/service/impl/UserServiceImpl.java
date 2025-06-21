package com.rngad33.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rngad33.web.constant.AESConstant;
import com.rngad33.web.constant.ErrorConstant;
import com.rngad33.web.constant.UserConstant;
import com.rngad33.web.exception.MyException;
import com.rngad33.web.manager.UserManager;
import com.rngad33.web.mapper.UserMapper;
import com.rngad33.web.model.dto.user.UserQueryRequest;
import com.rngad33.web.model.entity.User;
import com.rngad33.web.model.enums.misc.ErrorCodeEnum;
import com.rngad33.web.model.enums.user.UserStatusEnum;
import com.rngad33.web.service.UserService;
import com.rngad33.web.utils.AESUtils;
import com.rngad33.web.utils.LockUtils;
import com.rngad33.web.utils.SpecialCharValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户业务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserManager userManager;

    /**
     * 用户注册
     *
     * @param userName 账户
     * @param userPassword 密码
     * @param checkPassword 确认密码
     * @param planetCode 星球编号
     * @return 新账户id
     */
    @Override
    public Long userRegister(String userName, String userPassword, String checkPassword, String planetCode)
            throws Exception {
        // 1. 信息校验
        log.info("正在执行信息校验……");
        // - 字段不能为空
        if (StringUtils.isAnyBlank(userName, userPassword, checkPassword, planetCode)) {
            log.error(ErrorConstant.USER_HAVE_NULL_CHAR_MESSAGE);
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // - 长度限制
        if (userName.length() < 3 || userPassword.length() < 8) {
            log.error(ErrorConstant.LENGTH_ERROR_MESSAGE);
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // - 账户名称不能包含特殊字符
        if (SpecialCharValidator.doValidate(userName)) {
            log.error(ErrorConstant.USER_HAVE_SPECIAL_CHAR_MESSAGE);
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // - 密码和确认密码必须一致
        if (!userPassword.equals(checkPassword)) {
            log.error(ErrorConstant.PASSWD_NOT_REPEAT_MESSAGE);
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // - 星球编号限制总人数（总人数 = 10 ^ planetCode.length() - 1）
        if (planetCode.length() > 5) {
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }

        // - 加锁
        synchronized (LockUtils.getKeyLock(userName)) {
            // 2. 账户信息查重
            log.info("正在执行信息查重……");
            // - 名称查重
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName", userName);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                log.error(ErrorConstant.USER_NAME_ALREADY_EXIST_MESSAGE);
                throw new MyException(ErrorCodeEnum.PARAM_ERROR);
            }
            // - 编号查重
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("planetCode", planetCode);
            count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                log.error(ErrorConstant.PLANET_CODE_ALREADY_EXIST_MESSAGE);
                throw new MyException(ErrorCodeEnum.PARAM_ERROR);
            }

            // 3. 密码加密
            log.info("正在执行密码加密……");
            String encryptedPassword = AESUtils.doEncrypt(userPassword);
            if (encryptedPassword == null) {
                log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE);
                throw new MyException(ErrorCodeEnum.PARAM_ERROR);
            }

            // 4. 向数据库插入数据
            log.info("正在载入数据库……");
            User user = new User();
            user.setUserName(userName);
            user.setUserPassword(encryptedPassword);
            user.setPlanetCode(planetCode);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE);
                throw new MyException(ErrorCodeEnum.PARAM_ERROR);
            }

            // 5. 返回新账户id
            log.info("Correct! Successfully to register>>>");
            return user.getId();
        }
    }

    /**
     * 用户登录
     *
     * @param userName 账号
     * @param userPassword 密码
     * @param request http请求
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userName, String userPassword, HttpServletRequest request) throws Exception {
        // 1. 信息校验
        // - 字段不能为空
        if (StringUtils.isAnyBlank(userName, userPassword)) {
            log.error(ErrorConstant.USER_HAVE_NULL_CHAR_MESSAGE);
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // - 账户名称不能包含特殊字符
        if (SpecialCharValidator.doValidate(userName)) {
            log.error(ErrorConstant.USER_HAVE_SPECIAL_CHAR_MESSAGE);
            throw new MyException(ErrorCodeEnum.PARAM_ERROR);
        }
        // - 加锁
        synchronized (LockUtils.getKeyLock(userName)) {
            // 2. 密码加密
            String encryptedPassword = AESUtils.doEncrypt(userPassword);
            // 3. 连接数据库，核对用户信息
            User user;
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName", userName);
            queryWrapper.eq("userPassword", encryptedPassword);
            user = userMapper.selectOne(queryWrapper);
            // - 判断用户信息是否有误
            if (user == null) {
                log.error(ErrorConstant.USER_NOT_EXIST_OR_PASSWORD_ERROR_RETRY_MESSAGE);
                throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
            }
            // - 判断账户是否被封禁
            if (Objects.equals(user.getUserStatus(), UserStatusEnum.BAN_STATUS.getValue())) {
                log.error(ErrorConstant.USER_ALREADY_BAN_MESSAGE);
                throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
            }
            // 4. 登录态脱敏
            User safeUser = userManager.getSafeUser(user);
            // 5. 记录用户登录态
            request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safeUser);
            return safeUser;
        }
    }

    /**
     * 获取当前用户状态
     *
     * @param request http请求
     * @return 登录态
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new MyException(ErrorCodeEnum.USER_NOT_LOGIN);
        }
        long id = currentUser.getId();
        if (id <= 0) {
            throw new MyException(ErrorCodeEnum.PARAM_ERROR, "id异常！");
        }
        User user = this.getById(id);
        return userManager.getSafeUser(user);
    }

    /**
     * 退出登录
     *
     * @param request http请求
     * @return 状态码
     */
    @Override
    public Integer userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 0;
    }

    /**
     * 用户查询（仅管理员）
     *
     * @param userName 用户名
     * @return 用户列表
     */
    @Override
    public List<User> searchUsers(String userName, HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("userName", userName);   // 默认模糊查询
        List<User> userList = this.list(queryWrapper);
        return userList.stream()
                .filter(user -> !Objects.equals(user.getRole(), UserConstant.ADMIN_ROLE)) // 过滤掉管理员
                .map(user -> {
                    user.setUserPassword(AESConstant.CONFUSION);   // 密码保护
                    return user;
                }).collect(Collectors.toList());
    }

    /**
     * 用户封禁 / 解封（仅管理员）
     *
     * @param id 待封禁 / 解封用户id
     * @return 状态码
     */
    @Override
    public Integer userOrBan(@RequestBody Long id, HttpServletRequest request) {
        // 1. 查询用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            log.error(ErrorConstant.USER_NOT_EXIST_MESSAGE);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }

        // 2. 切换用户状态
        int currentStatus = user.getUserStatus();
        int newStatus = (currentStatus == 0) ? 1 : 0;
        user.setUserStatus(newStatus);

        // 3. 更新数据库
        int updateResult = userMapper.updateById(user);
        if (updateResult <= 0) {
            log.error(ErrorConstant.USER_LOSE_ACTION_MESSAGE);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }

        // 4. 返回操作结果
        if (newStatus != 0) {
            log.info("用户已封禁");
        } else {
            log.info("用户已解封");
        }
        return 0;
    }

}