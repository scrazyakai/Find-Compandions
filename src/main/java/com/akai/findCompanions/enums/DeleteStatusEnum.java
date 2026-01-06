package com.akai.findCompanions.enums;

/**
 * 删除状态枚举（用于 @TableLogic 逻辑删除）
 *
 * @author Recursion
 * @since 2026-01-06
 */
public enum DeleteStatusEnum {
    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),

    /**
     * 已删除
     */
    DELETED(1, "已删除");

    private final int value;
    private final String text;

    DeleteStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值获取枚举
     * @param value 状态值
     * @return 枚举对象
     */
    public static DeleteStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        DeleteStatusEnum[] statusList = DeleteStatusEnum.values();
        for (DeleteStatusEnum status : statusList) {
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
