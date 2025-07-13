package com.akai.findCompandions.controller;


import com.akai.findCompandions.common.BaseResponse;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.common.ResultUtils;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.dto.FriendRequestDTO;
import com.akai.findCompandions.model.dto.HandleFriendRequestDTO;
import com.akai.findCompandions.model.vo.FriendRequestListVO;
import com.akai.findCompandions.model.vo.FriendRequestVO;
import com.akai.findCompandions.model.vo.FriendVO;
import com.akai.findCompandions.service.IUserFriendService;
import com.akai.findCompandions.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 凯哥
 * @since 2025-07-08
 */
@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = {"http://localhost:3000"})
public class UserFriendController {
    @Resource
    IUserFriendService userFriendService;
    @Resource
    IUserService userService;
    @PostMapping("/request")
    public BaseResponse<FriendRequestVO> friendRequest(@RequestBody FriendRequestDTO friendRequestDTO){
        if(friendRequestDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        FriendRequestVO friendRequestVO = userFriendService.request(friendRequestDTO);
        return ResultUtils.success(friendRequestVO);
    }
    @PostMapping("/handle")
    public BaseResponse<Boolean> handleRequest(@RequestBody HandleFriendRequestDTO handleFriendRequestDTO){
        int status = handleFriendRequestDTO.getStatus();
        if(status < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userFriendService.handleRequest(handleFriendRequestDTO));

    }
    @GetMapping("/list")
    public BaseResponse<List<FriendVO>> list(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getUserLogin(request);
        List<FriendVO>  friendVOListlist = userFriendService.listFriends(loginUser);
        return ResultUtils.success(friendVOListlist);
    }
    @GetMapping("/list/requests")
    public BaseResponse<List<FriendRequestListVO>> listRequests(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getUserLogin(request);
        long userId = loginUser.getId();
        List<FriendRequestListVO> friendRequestVOList = userFriendService.listRequests(userId);
        return ResultUtils.success(friendRequestVOList);
    }


}
