package com.yupi.usercenter.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestVO {
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
     * 接收方Id
     */
    private Long toUserId;
    /**
     * 接收方昵称
     */
    private String toUsername;
    /**
     * 接收方头像
     */
    private String toAvatarUrl;
    /**
     * 发送时间
     */
    private Date sendTime;
}
