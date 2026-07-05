package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 投诉建议详情VO
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class ComplaintDetailVO {

    private Long id;

    private String complaintNo;

    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    private Long houseId;

    private String houseNumber;

    private String buildingName;

    private String communityName;

    private String type;

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