package com.example.spms.enums;

import lombok.Getter;

/**
 * 投诉建议类型枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum ComplaintType {

    NOISE(0, "噪音投诉"),
    CLEANING(1, "卫生投诉"),
    SECURITY(2, "安全投诉"),
    MAINTENANCE(3, "维修投诉"),
    SUGGESTION(4, "建议"),
    OTHER(5, "其他");

    private final int code;
    private final String desc;

    ComplaintType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ComplaintType fromCode(int code) {
        for (ComplaintType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return OTHER;
    }

    public static String getDescByCode(int code) {
        ComplaintType type = fromCode(code);
        return type != null ? type.desc : "其他";
    }
}