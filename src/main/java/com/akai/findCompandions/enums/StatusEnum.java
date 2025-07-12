package com.akai.findCompandions.enums;

public enum StatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");
    int value;
    String text;
    public static StatusEnum getEnumByValue(Integer value){
        if(value == null){
            return null;
        }
        StatusEnum[] statusList = StatusEnum.values();
        for(StatusEnum status : statusList){
            if(value == status.getValue()){
                return status;
            }
        }
        return null;
    }
    StatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
