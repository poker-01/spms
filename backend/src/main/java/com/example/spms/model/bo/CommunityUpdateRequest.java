package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改小区请求
 */
@Data
public class CommunityUpdateRequest {

    @NotNull(message = "小区ID不能为空")
    private Long id;

    private String communityCode;
    private String communityName;
    private String address;
    private String province;
    private String city;
    private String district;
    private Integer totalBuildings;
    private Integer totalUnits;
    private String propertyCompany;

    /** 负责人用户ID（物业管理员） */
    private Long managerId;

    private String managerName;
    private String managerPhone;
    private Integer status;
}