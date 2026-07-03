package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 业主端首页信息
 */
@Data
@Builder
public class OwnerHomeVO {

    private Long userId;
    private String userName;
    private String fullName;
    private String phoneNumber;
    private List<String> roles;
    private Integer pendingBillCount;
    private Integer repairCount;
    private Integer complaintCount;
}
