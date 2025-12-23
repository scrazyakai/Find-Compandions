package com.akai.findCompanions.model.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
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
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 标签 json 列表
     */
    private String tags;
}
