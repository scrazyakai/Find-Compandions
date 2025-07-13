package com.akai.findCompandions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.akai.findCompandions.common.ErrorCode;
import com.akai.findCompandions.entity.ChatPrivate;
import com.akai.findCompandions.entity.UserFriend;
import com.akai.findCompandions.exception.BusinessException;
import com.akai.findCompandions.mapper.UserFriendMapper;
import com.akai.findCompandions.model.domain.User;
import com.akai.findCompandions.model.dto.FriendRequestDTO;
import com.akai.findCompandions.model.dto.HandleFriendRequestDTO;
import com.akai.findCompandions.model.vo.FriendRequestListVO;
import com.akai.findCompandions.model.vo.FriendRequestVO;
import com.akai.findCompandions.model.vo.FriendVO;
import com.akai.findCompandions.service.IChatPrivateService;
import com.akai.findCompandions.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.akai.findCompandions.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 凯哥
 * @since 2025-07-08
 */
@Service
public class UserFriendServiceImpl extends ServiceImpl<UserFriendMapper, UserFriend> implements IUserFriendService {
    @Resource
    IUserService userService;
    @Resource
    IChatPrivateService chatPrivateService;
    /**
     * 发送好友申请
     * @param friendRequestDTO
     * @return
     */
    @Override
    public FriendRequestVO request(FriendRequestDTO friendRequestDTO) {
        if(friendRequestDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long fromUserId = friendRequestDTO.getFromUserId();
        User fromUser = userService.getById(fromUserId);
        long toUserId = friendRequestDTO.getToUserId();
        User toUser = userService.getById(toUserId);
        if(fromUserId == toUserId){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不能对自己发起好友申请");
        }
        if(toUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户不存在");
        }
        
        // 检查是否已经是好友
        QueryWrapper<UserFriend> friendCheckWrapper = new QueryWrapper<>();
        friendCheckWrapper.eq("userId", fromUserId)
                         .eq("friendId", toUserId)
                         .eq("status", 2); // 2表示已通过的好友关系
        UserFriend existingFriend = this.getOne(friendCheckWrapper);
        if(existingFriend != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "你们已经是好友了");
        }
        
        // 检查是否已经发送过待处理的申请
        QueryWrapper<UserFriend> requestCheckWrapper = new QueryWrapper<>();
        requestCheckWrapper.eq("userId", fromUserId)
                          .eq("friendId", toUserId)
                          .eq("status", 0); // 0表示待处理
        UserFriend existingRequest = this.getOne(requestCheckWrapper);
        if(existingRequest != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经发送过好友申请，请等待对方处理");
        }
        
        // 检查对方是否已经向你发送过申请
        QueryWrapper<UserFriend> reverseRequestWrapper = new QueryWrapper<>();
        reverseRequestWrapper.eq("userId", toUserId)
                           .eq("friendId", fromUserId)
                           .eq("status", 0); // 0表示待处理
        UserFriend reverseRequest = this.getOne(reverseRequestWrapper);
        if(reverseRequest != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对方已经向你发送了好友申请，请在好友申请列表中处理");
        }
        
        UserFriend userFriend = new UserFriend();
        userFriend.setUserId(friendRequestDTO.getFromUserId());
        userFriend.setStatus(0);
        userFriend.setCreateTime(new Date());
        userFriend.setUpdateTime(new Date());
        userFriend.setFriendId(toUserId);
        boolean success = save(userFriend);
        if(!success){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"申请失败");
        }
        FriendRequestVO friendRequestVO = new FriendRequestVO();
        //发送方Id，接收方Id，发送时间
        BeanUtils.copyProperties(friendRequestDTO,friendRequestVO);
        friendRequestVO.setFromUsername(fromUser.getUsername());
        friendRequestVO.setFromAvatarUrl(fromUser.getAvatarUrl());
        friendRequestVO.setToUsername(toUser.getUsername());
        friendRequestVO.setToAvatarUrl(toUser.getAvatarUrl());
        return friendRequestVO;
    }

    /**
     * 处理好友申请
     * @param handleFriendRequestDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleRequest(HandleFriendRequestDTO handleFriendRequestDTO) {
        int status = handleFriendRequestDTO.getStatus();
        if (status < 0 || handleFriendRequestDTO.getFromUserId() == null || handleFriendRequestDTO.getToUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        // 获取好友申请记录
        QueryWrapper<UserFriend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", handleFriendRequestDTO.getFromUserId());
        queryWrapper.eq("friendId", handleFriendRequestDTO.getToUserId());
        UserFriend userFriend = this.getOne(queryWrapper);
        if (userFriend == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "好友申请不存在");
        }

        // 更新好友申请状态
        userFriend.setStatus(status);
        userFriend.setUpdateTime(new Date());
        boolean success = this.updateById(userFriend);

        // 如果通过，创建反向好友记录
        if (status == 2) {
            UserFriend reverse = new UserFriend();
            reverse.setUserId(handleFriendRequestDTO.getToUserId());
            reverse.setFriendId(handleFriendRequestDTO.getFromUserId());
            reverse.setStatus(2);
            reverse.setCreateTime(new Date());
            reverse.setUpdateTime(new Date());
            success &= this.save(reverse); // 确保两个操作都成功
            //通过好友后发送我们已经成为好友了，开始聊天吧
            String message = "我们已经成为好友了，快来聊天吧";
            ChatPrivate chatPrivate = new ChatPrivate();
            chatPrivate.setFromUserId(handleFriendRequestDTO.getToUserId());
            chatPrivate.setToUserId(handleFriendRequestDTO.getFromUserId());
            chatPrivate.setSendTime(new Date());
            chatPrivate.setContent(message);
            chatPrivateService.save(chatPrivate);

        }

        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "处理失败");
        }

        return true;

    }

    @Override
    public List<FriendVO> listFriends(User loginUser) {
        long userId = loginUser.getId();
        if(userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserFriend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<UserFriend> list = list(queryWrapper);
        if(list == null){
            return Collections.emptyList();
        }
        List<FriendVO> result = new ArrayList<>();
        for(UserFriend userFriend : list){
            Integer status = userFriend.getStatus();
            Long friendId = userFriend.getFriendId();
            User toUser = userService.getById(friendId);
            if(toUser == null){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            if(status == 2){
                FriendVO friendVO = new FriendVO();
                friendVO.setFromUserId(userId);
                friendVO.setFromUsername(loginUser.getUsername());
                friendVO.setFromAvatarUrl(loginUser.getAvatarUrl());
                friendVO.setToUserId(friendId);
                friendVO.setToUsername(toUser.getUsername());
                friendVO.setToAvatarUrl(toUser.getAvatarUrl());
                result.add(friendVO);
            }

        }
        return result;
    }

    @Override
    public List<FriendRequestListVO> listRequests(long userId) {
        if(userId <= 0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int status = 0;
        QueryWrapper<UserFriend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("friendId",userId).eq("status",status);
        List<UserFriend> list = list(queryWrapper);
        List<FriendRequestListVO> result = new ArrayList<>();
        if(list == null){
            return Collections.emptyList();
        }
        for(UserFriend friend : list){
            FriendRequestListVO friendRequestVO = new FriendRequestListVO();
            User fromUser = userService.getById(friend.getUserId());
            friendRequestVO.setFromUserId(fromUser.getId());
            friendRequestVO.setFromUsername(fromUser.getUsername());
            friendRequestVO.setFromAvatarUrl(fromUser.getAvatarUrl());
            friendRequestVO.setSendTime(friend.getCreateTime());
            result.add(friendRequestVO);
        }
        return result;
    }
}
