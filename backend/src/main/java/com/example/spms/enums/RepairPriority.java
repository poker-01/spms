package com.example.spms.enums;

import lombok.Getter;

/**
 * 报修优先级枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum RepairPriority {

    LOW(0, "低"),
    MEDIUM(1, "中"),
    HIGH(2, "高"),
    URGENT(3, "紧急");

    private final int code;
    private final String desc;

    RepairPriority(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RepairPriority fromCode(int code) {
        for (RepairPriority priority : values()) {
            if (priority.code == code) {
                return priority;
            }
        }
        return MEDIUM;
    }

    public static String getDescByCode(int code) {
        RepairPriority priority = fromCode(code);
        return priority != null ? priority.desc : "中";
    }
}