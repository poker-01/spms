package com.example.spms.enums;

import lombok.Getter;

/**
 * 报修工单状态枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum RepairStatus {

    WAITING(0, "待派单"),
    ASSIGNED(1, "已派单"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    CLOSED(5, "已关闭");

    private final int code;
    private final String desc;

    RepairStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RepairStatus fromCode(int code) {
        for (RepairStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static String getDescByCode(int code) {
        RepairStatus status = fromCode(code);
        return status != null ? status.desc : "未知状态";
    }
}