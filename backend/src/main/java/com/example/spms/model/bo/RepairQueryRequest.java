package com.example.spms.model.bo;

import lombok.Data;

/**
 * 报修工单查询请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class RepairQueryRequest {

    private String orderNo;

    private Integer status;

    private Integer repairType;

    private Long ownerId;

    private Long assigneeId;

    private String startTime;

    private String endTime;

    private Integer page = 1;

    private Integer size = 10;
}