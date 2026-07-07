package com.example.spms.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * 小区响应对象
 */
@Data
public class CommunityVO {

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
    private Long managerId;
    private String managerName;
    private String managerPhone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}