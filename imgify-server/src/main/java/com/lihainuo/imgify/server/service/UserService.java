package com.lihainuo.imgify.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lihainuo.imgify.server.model.dto.user.UserQueryRequest;
import com.lihainuo.imgify.server.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lihainuo.imgify.server.model.vo.LoginUserVO;
import com.lihainuo.imgify.server.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author leehainuo
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-07-22 16:14:40
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount   - 用户账号
     * @param userPassword  - 用户密码
     * @param checkPassword - 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount  - 用户账号
     * @param userPassword - 用户密码
     * @param request      - HTTP请求，方便设置 cookie
     * @return 脱敏后的用户数据
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @param request - HTTP请求
     * @return 注销结果
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request - HTTP请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param userPassword - 原始密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取脱敏后的用户数据
     * @param user - 用户数据
     * @return 脱敏后的登录用户数据
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏后的用户数据
     * @param user - 用户数据
     * @return 脱敏后的用户数据
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户列表数据
     * @param userList - 用户列表数据
     * @return 脱敏后的用户列表数据
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 将查询请求转换为查询条件
     * @param userQueryRequest - 查询请求
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 判断当前用户是否为管理员
     * @param user
     * @return 是否为管理员结果
     */
    boolean isAdmin(User user);
}
