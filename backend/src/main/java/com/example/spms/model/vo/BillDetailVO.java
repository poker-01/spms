package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账单详情VO
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class BillDetailVO {

    private Long id;

    private String billNo;

    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    private Long houseId;

    private String houseNumber;

    private String buildingName;

    private String communityName;

    private Long feeItemId;

    private String itemName;

    private String itemCode;

    private String billPeriod;

    private BigDecimal billAmount;

    private BigDecimal paidAmount;

    private Integer status;

    private String statusName;

    private String payDeadline;

    private String payTime;

    private Integer payMethod;

    private String payMethodName;

    private String createTime;

    private String updateTime;

    /** 缴费记录列表 */
    private List<PaymentRecordVO> paymentRecords;
}