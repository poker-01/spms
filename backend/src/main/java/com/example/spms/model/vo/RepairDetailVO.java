package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报修工单详情VO
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class RepairDetailVO {

    private Long id;

    private String orderNo;

    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    private Long houseId;

    private String houseNumber;

    private String buildingName;

    private String communityName;

    private String repairType;

    private String repairTypeName;

    private String repairDesc;

    private String repairPhone;

    private Integer priority;

    private String priorityName;

    private Integer status;

    private String statusName;

    private Long assigneeId;

    private String assigneeName;

    private String assigneePhone;

    private String assignTime;

    private String repairTime;

    private BigDecimal repairCost;

    private String repairResult;

    private Integer evaluateScore;

    private String evaluateComment;

    private String createTime;

    private String updateTime;
}