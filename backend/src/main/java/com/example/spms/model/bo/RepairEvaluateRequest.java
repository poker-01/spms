package com.example.spms.model.bo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修评价请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class RepairEvaluateRequest {

    @NotNull(message = "工单ID不能为空")
    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer score;

    private String comment;
}