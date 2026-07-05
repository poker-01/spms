package com.example.spms.model.bo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 生成账单请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class BillGenerateRequest {

    @NotNull(message = "费用项目ID不能为空")
    private Long feeItemId;

    @NotEmpty(message = "业主ID列表不能为空")
    private List<Long> ownerIds;

    @NotNull(message = "账单周期不能为空")
    private String billPeriod;

    @NotNull(message = "账单金额不能为空")
    private BigDecimal billAmount;

    private LocalDate payDeadline;

    private String remark;
}