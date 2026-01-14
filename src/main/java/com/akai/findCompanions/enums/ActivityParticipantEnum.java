package com.akai.findCompanions.enums;

public enum ActivityParticipantEnum {
    JOINED("已经入",0),
    QUITED("已退出",1);
    private final int value;
    private final String text;
    ActivityParticipantEnum(String text, int value) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }


    public String getText() {
        return text;
    }
}
