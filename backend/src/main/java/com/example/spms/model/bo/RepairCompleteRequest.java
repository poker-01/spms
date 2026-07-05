package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报修完成请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class RepairCompleteRequest {

    @NotNull(message = "工单ID不能为空")
    private Long orderId;

    private String repairResult;

    private BigDecimal repairCost;
}