package com.akai.findCompanions.enums;

public enum ActivityStatusEnum {
    NOT_STARTED(1, "未开始"),
    PROCESSING(2, "进行中"),
    CANCELLED(3, "已取消"),
    ENDED(4, "已结束");
    int value;
    String text;

    public static ActivityStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        ActivityStatusEnum[] statusList = ActivityStatusEnum.values();
        for (ActivityStatusEnum status : statusList) {
            if (value == status.getValue()) {
                return status;
            }
        }
        return null;
    }

    ActivityStatusEnum(int value, String text) {
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
