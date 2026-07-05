package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 账单VO（列表展示）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class BillVO {

    private Long id;

    private String billNo;

    private String ownerName;

    private String ownerPhone;

    private String houseNumber;

    private String buildingName;

    private String itemName;

    private String billPeriod;

    private BigDecimal billAmount;

    private BigDecimal paidAmount;

    private Integer status;

    private String statusName;

    private String payDeadline;

    private String payTime;

    private String createTime;

    private String updateTime;
}