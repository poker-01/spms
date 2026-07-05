package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 投诉建议VO（列表展示）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class ComplaintVO {

    private Long id;

    private String complaintNo;

    private String ownerName;

    private String ownerPhone;

    private String houseNumber;

    private Integer type;

    private String typeName;

    private String title;

    private String content;

    private String contactPhone;

    private Integer status;

    private String statusName;

    private String replyContent;

    private String replyTime;

    private String createTime;

    private String updateTime;
}