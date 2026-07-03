package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 业主端-投诉建议信息
 */
@Data
@Builder
public class OwnerComplaintVO {

    private Long id;
    private String complaintNo;
    private Integer type;
    private String title;
    private String content;
    private String contactPhone;
    private Integer status;
    private String replyContent;
    private String replyTime;
    private String createTime;
}
