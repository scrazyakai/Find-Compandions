package com.akai.findCompanions.model.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private String username;
    private String token;
    private Long userId;
}
