package com.akai.findCompandions.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ChatGroupVO {
    private Long id;                // 消息ID
    private Long userId;            // 发送者ID
    private String username;        // 发送者昵称
    private String avatarUrl;       // 发送者头像
    private String content;         // 消息内容
    private Date sendTime; // 发送时间
}