package com.example.spms.model.bo;

import lombok.Data;

/**
 * 巡检记录查询请求（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class InspectionQueryRequest {

    private String location;

    private Integer result;

    private Long inspectorId;

    private String startTime;

    private String endTime;

    private Integer page = 1;

    private Integer size = 10;
}