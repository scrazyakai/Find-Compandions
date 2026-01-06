package com.akai.findCompanions.enums;

/**
 * 评论级别枚举
 *
 * @author Recursion
 * @since 2026-01-06
 */
public enum CommentLevelEnum {
    /**
     * 一级评论
     */
    FIRST_LEVEL(1, "一级评论"),

    /**
     * 二级评论（回复）
     */
    SECOND_LEVEL(2, "二级评论");

    private final int value;
    private final String text;

    CommentLevelEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     * @param value 级别值
     * @return 枚举对象
     */
    public static CommentLevelEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        CommentLevelEnum[] levelList = CommentLevelEnum.values();
        for (CommentLevelEnum level : levelList) {
            if (value == level.getValue()) {
                return level;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
