package com.akai.findCompandions.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ChatPrivateVO {
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
     * 发送的内容
     */
    private String content;
    /**
     * 发送时间
     */
    private Date sendTime;
}