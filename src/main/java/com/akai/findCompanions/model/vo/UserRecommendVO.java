package com.akai.findCompanions.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserRecommendVO {
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

    /**
     * 标签列表
     */
    private List<String> tags;
    /**
     * 简介
     */

    private String profile;
}
