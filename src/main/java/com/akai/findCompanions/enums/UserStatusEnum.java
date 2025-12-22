package com.akai.findCompanions.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    BANNED(1, "禁用");
    int value;
    String text;
    UserStatusEnum(int value, String text){
        this.value = value;
        this.text = text;
    }
    public static UserStatusEnum getEnumByValue(Integer value){
        if(value == null){
            return null;
        }
        UserStatusEnum[] statusList = UserStatusEnum.values();
        for(UserStatusEnum status : statusList){
            if(value == status.getValue()){
                return status;
            }
        }
        return null;
    }
    public static int getValueByEnum(UserStatusEnum status){
        if(status == null){
            return 0;
        }
        return status.getValue();
    }

}
