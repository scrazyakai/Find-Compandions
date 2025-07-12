package com.akai.findCompandions.model.dto;

import lombok.Data;

@Data
public class Message {
    private String type; // "private" or "group"
    private Long fromUserId;
    private Long toUserId; // for private messages
    private String content;

    // Getters and Setters
}
