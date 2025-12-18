package com.akai.findCompandions.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akai.findCompandions.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author 凯哥
 * @since 2025-06-04
 */
public interface IUserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     *
     * @return
     */
    boolean userLogout(long userId);

    /**
     * 根据比钱查找用户
     * @param tagList
     * @return
     */
    List<User> searchUserByTags(List<String> tagList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user);

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    User getUserLogin(HttpServletRequest request);

    /**
     * 判断是不是管理员
     * @return
     */
    boolean isAdmin();

    /**
     * 推荐用户
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    Page<User> recommedUsers(long pageNum, long pageSize,HttpServletRequest request);
}
