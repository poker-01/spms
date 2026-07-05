package com.example.spms.enums;

import lombok.Getter;

/**
 * 投诉建议状态枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum ComplaintStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    REPLIED(2, "已回复"),
    CLOSED(3, "已关闭");

    private final int code;
    private final String desc;

    ComplaintStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ComplaintStatus fromCode(int code) {
        for (ComplaintStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }

    public static String getDescByCode(int code) {
        ComplaintStatus status = fromCode(code);
        return status != null ? status.desc : "未知状态";
    }
}