package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改费用项目请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class FeeItemUpdateRequest {

    @NotNull(message = "费用项目ID不能为空")
    private Long id;

    private String itemCode;

    private String itemName;

    private Integer itemType;

    private BigDecimal unitPrice;

    private String unit;

    private Integer calcMethod;

    private Integer isDefault;

    private Integer status;
}