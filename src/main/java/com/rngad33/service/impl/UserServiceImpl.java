package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.constant.AESConstant;
import com.yupi.usercenter.constant.ErrorConstant;
import com.yupi.usercenter.enums.ErrorCodeEnum;
import com.yupi.usercenter.enums.UserRoleEnum;
import com.yupi.usercenter.enums.UserStatusEnum;
import com.yupi.usercenter.exception.MyException;
import com.yupi.usercenter.mapper.UserMapper;
import com.yupi.usercenter.model.User;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.utils.AESUtils;
import com.yupi.usercenter.utils.SpecialCharValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 业务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

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
    public Long userRegister(String userName, String userPassword, String checkPassword, String planetCode) throws Exception {
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

        // 单机锁
        synchronized (userName.intern()) {
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

        // 2. 密码加密
        String encryptedPassword = AESUtils.doEncrypt(userPassword);

        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", userName);
        queryWrapper.eq("userPassword", encryptedPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.error(ErrorConstant.USER_NOT_EXIST_MESSAGE);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }

        // 4. 判断账户是否被封禁
        if (Objects.equals(user.getUserStatus(), UserStatusEnum.BAN_STATUS.getValue())) {
            log.error(ErrorConstant.USER_ALREADY_BAN_MESSAGE);
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }

        // 5. 信息脱敏
        User safeUser = getSafeUser(user);

        // 6. 记录用户登录态（已脱敏）
        request.getSession().setAttribute(UserRoleEnum.USER_LOGIN_STATE.getKey(), safeUser);
        return safeUser;
    }

    /**
     * 获取当前用户状态
     *
     * @param request http请求
     * @return 登录态
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserRoleEnum.USER_LOGIN_STATE.getKey());
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }
        long id = currentUser.getId();
        if (id <= 0) {
            throw new MyException(ErrorCodeEnum.USER_LOSE_ACTION);
        }
        User user = this.getById(id);
        return getSafeUser(user);
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
        request.getSession().removeAttribute(UserRoleEnum.USER_LOGIN_STATE.getKey());
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
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like("userName", userName);   // 默认模糊查询
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(user -> {
            user.setUserPassword(AESConstant.CONFUSION);   // 密码保护
            return user;
        }).collect(Collectors.toList());
    }

    /**
     * 用户封禁 / 解封（仅管理员）
     *
     * @param id 待封禁/解封用户id
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

    /**
     * 用户注销（当前仅管理员，从数据库中彻底删除账户）
     *
     * @param id 待注销用户id
     * @return 状态码
     */
    @Override
    public Integer userLogoff(Long id, HttpServletRequest request) {
        // 1. 查询用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            log.error(ErrorConstant.USER_NOT_EXIST_MESSAGE);
            return -1;
        }
        // 2. 从数据库中删除账户
        // todo 前往Mapper自定义sql语句，或者合并到删除板块
        return 0;
    }

    /**
     * 用户脱敏
     * 使用掩码隐藏敏感信息，保障传输层安全
     * @param user 脱敏前的账户
     * @return 脱敏后的账户
     */
    private static User getSafeUser(User user) {
        if (user == null) return null;
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setPlanetCode(user.getPlanetCode());
        safeUser.setRole(user.getRole());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setUserPassword(AESConstant.CONFUSION);
        safeUser.setAge(user.getAge());
        safeUser.setPhone(AESConstant.CONFUSION);
        safeUser.setEmail(user.getEmail());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(new Date());
        return safeUser;
    }

}