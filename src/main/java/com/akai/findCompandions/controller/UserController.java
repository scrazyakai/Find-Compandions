package com.akai.findCompandions.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.akai.findCompandions.common.BaseResponse;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.common.ResultUtils;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.request.UserLoginRequest;
import com.akai.findCompandions.model.request.UserRegisterAndLogin;
import com.akai.findCompandions.model.request.UserRegisterRequest;
import com.akai.findCompandions.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@CrossOrigin(origins = {"http://localhost:3000"})
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
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }
    @PostMapping("/registerAndLogin")
    public BaseResponse<User> userRegisterAndLogin(
            @RequestBody UserRegisterAndLogin req,
            HttpServletRequest request) {

        // 1. 入参校验
        if (req == null
                || StringUtils.isAnyBlank(req.getUserAccount(), req.getUserPassword(), req.getCheckPassword())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空或不完整");
        }
        if (!req.getUserPassword().equals(req.getCheckPassword())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        // 2. 执行注册
        long registerResult = userService.userRegister(
                req.getUserAccount(),
                req.getUserPassword(),
                req.getCheckPassword()
        );
        // 如果注册返回值是 -1 之类的错误码（根据你的 userService 逻辑），直接返回错误
        if (registerResult < 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "注册失败，账号可能已存在");
        }

        // 3. 注册成功后，执行登录
        User user = userService.userLogin(
                req.getUserAccount(),
                req.getUserPassword(),
                request
        );

        if (user == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "注册成功，但登录失败");
        }

        // 4. 返回登录用户信息
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
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

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
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
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //判断user是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userService.updateUser(user,request);
        return ResultUtils.success(result);
    }
    //TODO 专门为修改头像写一个接口，上传头像到阿里OSS
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageNum, long pageSize,HttpServletRequest request){
        Page<User> userList = userService.recommedUsers(pageNum,pageSize,request);
        return ResultUtils.success(userList);
    }
    @GetMapping("/match")
    public BaseResponse<List<User>> matchesUsers(long num, HttpServletRequest request){
        if(num <= 0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getUserLogin(request);
        return ResultUtils.success(userService.machesUsers(num,loginUser));
    }

}
