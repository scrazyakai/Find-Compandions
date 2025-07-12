package com.akai.findCompandions.model.dto;

import lombok.Data;

@Data
public class HandleFriendRequestDTO {
    /***
     * 处理状态
     */
    int status;
    /**
     * 发送者Id
     */
    Long fromUserId;
    /**
     * 接收方ID
     */
    Long toUserId;

}
