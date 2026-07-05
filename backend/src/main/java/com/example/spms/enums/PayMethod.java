package com.example.spms.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Getter
public enum PayMethod {

    CASH(0, "现金"),
    BANK_TRANSFER(1, "银行转账"),
    WECHAT(2, "微信支付"),
    ALIPAY(3, "支付宝"),
    OTHER(4, "其他");

    private final int code;
    private final String desc;

    PayMethod(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayMethod fromCode(int code) {
        for (PayMethod method : values()) {
            if (method.code == code) {
                return method;
            }
        }
        return OTHER;
    }

    public static String getDescByCode(int code) {
        PayMethod method = fromCode(code);
        return method != null ? method.desc : "其他";
    }
}