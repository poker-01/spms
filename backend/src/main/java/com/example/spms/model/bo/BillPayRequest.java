package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 缴费登记请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class BillPayRequest {

    @NotNull(message = "账单ID不能为空")
    private Long billId;

    @NotNull(message = "缴费金额不能为空")
    private BigDecimal payAmount;

    @NotNull(message = "支付方式不能为空")
    private Integer payMethod;

    private String receiptNo;
}