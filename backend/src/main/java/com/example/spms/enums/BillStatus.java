package com.example.spms.enums;

import lombok.Getter;

/**
 * 账单状态枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum BillStatus {

    UNPAID(0, "待缴费"),
    PARTIAL(1, "部分缴费"),
    PAID(2, "已缴费"),
    OVERDUE(3, "已逾期");

    private final int code;
    private final String desc;

    BillStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BillStatus fromCode(int code) {
        for (BillStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNPAID;
    }

    public static String getDescByCode(int code) {
        BillStatus status = fromCode(code);
        return status != null ? status.desc : "未知状态";
    }
}