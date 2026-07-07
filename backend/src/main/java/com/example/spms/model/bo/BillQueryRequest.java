package com.example.spms.model.bo;

import lombok.Data;

/**
 * 账单查询请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class BillQueryRequest {

    private String billNo;

    private Long ownerId;

    private Long houseId;

    private Long feeItemId;

    private Integer status;

    private String billPeriod;

    private String startTime;

    private String endTime;

    private Integer page = 1;

    private Integer size = 10;

    /**
     * 小区ID，用于数据隔离（非超级管理员只能查看本小区账单）
     */
    private Long communityId;
}