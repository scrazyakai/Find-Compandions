package com.yupi.usercenter.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FriendRequestDTO {
    /**
     * 发送方Id
     */
    Long fromUserId;
    /**
     * 接收方Id
     */
    Long toUserId;
    /**
     * 发送时间
     */
    Date sendTime;


}
