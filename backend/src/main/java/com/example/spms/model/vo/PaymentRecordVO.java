package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 缴费记录VO
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class PaymentRecordVO {

    private Long id;

    private String paymentNo;

    private Long billId;

    private String billNo;

    private Long ownerId;

    private String ownerName;

    private String houseNumber;

    private BigDecimal payAmount;

    private Integer payMethod;

    private String payMethodName;

    private String payTime;

    private String operatorName;

    private String receiptNo;

    private String createTime;
}