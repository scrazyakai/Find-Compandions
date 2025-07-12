package com.akai.findCompandions.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestListVO {
    /**
     * 发送方Id
     */
    private Long fromUserId;
    /**
     * 发送方昵称
     */
    private String fromUsername;
    /**
     * 发送方头像
     */
    private String fromAvatarUrl;
    /**
     * 发送时间
     */
    private Date sendTime;
}
