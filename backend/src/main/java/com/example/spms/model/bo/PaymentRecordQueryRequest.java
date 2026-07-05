package com.example.spms.model.bo;

import lombok.Data;

/**
 * 缴费记录查询请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class PaymentRecordQueryRequest {

    private String paymentNo;

    private Long billId;

    private Long ownerId;

    private Long houseId;

    private Integer payMethod;

    private String startTime;

    private String endTime;

    private Integer page = 1;

    private Integer size = 10;
}