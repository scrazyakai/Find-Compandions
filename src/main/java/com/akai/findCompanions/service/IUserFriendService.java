package com.akai.findCompanions.service;

import com.akai.findCompanions.model.domain.UserFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.akai.findCompanions.model.domain.User;
import com.akai.findCompanions.model.dto.FriendRequestDTO;
import com.akai.findCompanions.model.dto.HandleFriendRequestDTO;
import com.akai.findCompanions.model.vo.FriendRequestListVO;
import com.akai.findCompanions.model.vo.FriendRequestVO;
import com.akai.findCompanions.model.vo.FriendVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 凯哥
 * @since 2025-07-08
 */
public interface IUserFriendService extends IService<UserFriend> {
    /**
     * 发送好友申请
     * @param friendRequestDTO
     */
    FriendRequestVO request(FriendRequestDTO friendRequestDTO);

    /**
     * 处理好友申请
     * @param handleFriendRequestDTO
     * @return
     */
    Boolean handleRequest(HandleFriendRequestDTO handleFriendRequestDTO);

    /**
     * 查询好友
     * @param
     * @return
     */
    List<FriendVO> listFriends(User loginUser);

    /**
     * 查询好友申请
     * @param userId
     * @return
     */
    List<FriendRequestListVO> listRequests(long userId);
}
