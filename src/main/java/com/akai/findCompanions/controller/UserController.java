package com.akai.findCompanions.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.akai.findCompanions.model.request.UserUpdateRequest;
import com.akai.findCompanions.model.vo.UserLoginVO;
import com.akai.findCompanions.model.vo.UserRecommendVO;

import com.akai.findCompanions.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.akai.findCompanions.common.BaseResponse;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.model.request.UserLoginRequest;
import com.akai.findCompanions.model.request.UserRegisterRequest;
import com.akai.findCompanions.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Boolean> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        if (result > 0) {
            return ResultUtils.success(true);
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号注册失败");
        }

    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO userLoginVO = userService.userLogin(userAccount, userPassword);
        return ResultUtils.success(userLoginVO);
    }
    /**
     * 用户注销
     *
     * @return
     */
    @SaCheckLogin
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout() {
        long userId = StpUtil.getLoginIdAsLong();
        boolean result = userService.userLogout(userId);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @GetMapping("/search")
    //TODO ES实现
    public BaseResponse<List<User>> searchUsers(String username) {
        if (!userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    @SaCheckLogin
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser() {
        long userId = StpUtil.getLoginIdAsLong();
        User loginUser = userService.getById(userId);
        if (userId <= 0 || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StpUtil.kickout(userId);
        boolean b = userService.removeById(userId);
        return ResultUtils.success(b);
    }

    @SaCheckLogin
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        if(userUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        //判断user是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        BeanUtils.copyProperties(userUpdateRequest,user);
        int result = userService.updateUser(user);
        return ResultUtils.success(result);
    }
    @SaCheckLogin
    @GetMapping("/recommend")
    public BaseResponse<List<UserRecommendVO>> recommendUsers() {
        //检查是否登录
        long userId = StpUtil.getLoginIdAsLong();
        User loginUser = userService.getById(userId);
        if(loginUser == null || StringUtils.isEmpty(loginUser.getTags())){
            return ResultUtils.success(Collections.emptyList());
        }
        List<UserRecommendVO> result = userService.recommedUsers(loginUser);

        return ResultUtils.success(result);
    }

}
