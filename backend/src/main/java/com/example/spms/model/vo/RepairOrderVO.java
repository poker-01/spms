package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报修工单VO（列表展示）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class RepairOrderVO {

    private Long id;

    private String orderNo;

    private String ownerName;

    private String ownerPhone;

    private String houseNumber;

    private String repairTypeName;

    private String repairType;

    private String repairDesc;

    private String repairPhone;

    private String priorityName;

    private Integer priority;

    private String statusName;

    private Integer status;

    private String assigneeName;

    private BigDecimal repairCost;

    private Integer evaluateScore;

    private String assignTime;

    private String repairTime;

    private String createTime;

    private String updateTime;
}