package com.example.spms.model.bo;

import lombok.Data;

/**
 * 投诉建议查询请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class ComplaintQueryRequest {

    private String complaintNo;

    private Integer status;

    private String type;

    private Long ownerId;

    private String startTime;

    private String endTime;

    private Integer page = 1;

    private Integer size = 10;
}