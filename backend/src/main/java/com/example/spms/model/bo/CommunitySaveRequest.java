package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增小区请求
 */
@Data
public class CommunitySaveRequest {

    @NotBlank(message = "小区编码不能为空")
    private String communityCode;

    @NotBlank(message = "小区名称不能为空")
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