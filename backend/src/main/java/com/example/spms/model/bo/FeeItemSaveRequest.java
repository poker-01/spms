package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增费用项目请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class FeeItemSaveRequest {

    @NotBlank(message = "费用项目编码不能为空")
    private String itemCode;

    @NotBlank(message = "费用项目名称不能为空")
    private String itemName;

    @NotNull(message = "费用类型不能为空")
    private Integer itemType;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    private String unit;

    private Integer calcMethod;

    private Integer isDefault;

    private Integer status;
}