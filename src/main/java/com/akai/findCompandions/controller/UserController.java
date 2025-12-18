package com.akai.findCompandions.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akai.findCompandions.common.BaseResponse;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.common.ResultUtils;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.request.UserLoginRequest;
import com.akai.findCompandions.model.request.UserRegisterRequest;
import com.akai.findCompandions.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.akai.findCompandions.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/user")
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
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
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword);
        return ResultUtils.success(user);
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
    public BaseResponse<User> getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
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
    public BaseResponse<Boolean> deleteUser(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam String tagNameList){
        if(StringUtils.isBlank(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将 "大二,Python" → ["大二","Python"]
        List<String> tagNames = Arrays.stream(tagNameList.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<User> users = userService.searchUserByTags(tagNames);
        return ResultUtils.success(users);
    }
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user){
        //判断user是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userService.updateUser(user);
        return ResultUtils.success(result);
    }
    //TODO 专门为修改头像写一个接口，上传头像到阿里OSS
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageNum, long pageSize,HttpServletRequest request){
        Page<User> userList = userService.recommedUsers(pageNum,pageSize,request);
        return ResultUtils.success(userList);
    }
    //TODO 使用ES实现
    @GetMapping("/match")
    public BaseResponse<List<User>> matchesUsers(long num, HttpServletRequest request){
        return ResultUtils.success(new ArrayList<>());
    }

}
