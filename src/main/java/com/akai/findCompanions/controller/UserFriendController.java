package com.akai.findCompanions.controller;


import com.akai.findCompanions.common.BaseResponse;
import com.akai.findCompanions.common.ErrorCode;
import com.akai.findCompanions.common.ResultUtils;
import com.akai.findCompanions.exception.BusinessException;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.model.dto.FriendRequestDTO;
import com.akai.findCompanions.model.dto.HandleFriendRequestDTO;
import com.akai.findCompanions.model.vo.FriendRequestListVO;
import com.akai.findCompanions.model.vo.FriendRequestVO;
import com.akai.findCompanions.model.vo.FriendVO;
import com.akai.findCompanions.service.IUserFriendService;
import com.akai.findCompanions.service.IUserService;
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
