package com.yupi.usercenter.model.request;

import lombok.Data;

@Data
public class UserRegisterAndLogin {
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 校验密码
     */
    private String checkPassword;
}
