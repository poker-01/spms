package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 业主账单信息
 */
@Data
@Builder
public class OwnerBillVO {

    private Long id;
    private String billNo;
    private String itemName;
    private BigDecimal amount;
    private Integer status;
    private String createTime;
    private String deadline;
}
