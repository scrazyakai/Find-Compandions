package com.akai.findCompanions.model.vo;

import lombok.Data;

@Data
public class UserCardVO {
    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;
}
