package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 费用项目VO
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class FeeItemVO {

    private Long id;

    private String itemCode;

    private String itemName;

    private Integer itemType;

    private String itemTypeName;

    private BigDecimal unitPrice;

    private String unit;

    private Integer calcMethod;

    private String calcMethodName;

    private Integer isDefault;

    private Integer status;

    private String statusName;

    private String createTime;

    private String updateTime;
}