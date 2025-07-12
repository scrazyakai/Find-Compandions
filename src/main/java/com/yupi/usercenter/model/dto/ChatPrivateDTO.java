package com.yupi.usercenter.model.dto;

import com.yupi.usercenter.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class ChatPrivateDTO implements Serializable {
    private static final long serialVersionUID = 3938148708218454990L;
    /**
     * 发送方
     */
    Long fromUserId;
    /**
     * 接收方
     */
    Long toUserId;
    /**
     * 发送的内容
     */
    String content;
    /**
     * 发送时间
     */
    Date sendTime;
}
