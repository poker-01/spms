package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修派单请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class RepairAssignRequest {

    @NotNull(message = "工单ID不能为空")
    private Long orderId;

    @NotNull(message = "维修人员ID不能为空")
    private Long assigneeId;
}