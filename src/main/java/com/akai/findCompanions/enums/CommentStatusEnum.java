package com.akai.findCompanions.enums;

/**
 * 评论状态枚举
 *
 * @author Recursion
 * @since 2026-01-06
 */
public enum CommentStatusEnum {
    /**
     * 未删除（正常显示）
     */
    NOT_DELETED(0, "未删除"),

    /**
     * 已删除
     */
    DELETED(1, "已删除");

    private final int value;
    private final String text;

    CommentStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     * @param value 状态值
     * @return 枚举对象
     */
    public static CommentStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        CommentStatusEnum[] statusList = CommentStatusEnum.values();
        for (CommentStatusEnum status : statusList) {
            if (value == status.getValue()) {
                return status;
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
