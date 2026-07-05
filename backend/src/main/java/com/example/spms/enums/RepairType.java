package com.example.spms.enums;

import lombok.Getter;

/**
 * 报修类型枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum RepairType {

    ELECTRIC(0, "电路维修"),
    WATER(1, "水管维修"),
    LOCK(2, "门锁维修"),
    ELEVATOR(3, "电梯维修"),
    HVAC(4, "空调维修"),
    OTHER(5, "其他");

    private final int code;
    private final String desc;

    RepairType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RepairType fromCode(int code) {
        for (RepairType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return OTHER;
    }

    public static String getDescByCode(int code) {
        RepairType type = fromCode(code);
        return type != null ? type.desc : "其他";
    }
}