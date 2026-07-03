package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 业主端-报修工单信息
 */
@Data
@Builder
public class OwnerRepairVO {

    private Long id;
    private String orderNo;
    private Integer repairType;
    private String repairDesc;
    private String repairPhone;
    private Integer priority;
    private Integer status;
    private BigDecimal repairCost;
    private Integer evaluateScore;
    private String evaluateComment;
    private String createTime;
    private String repairTime;
}
