package com.example.spms.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * 楼栋响应对象
 */
@Data
public class BuildingVO {

    private Long id;
    private Long communityId;
    private String communityName;  // 冗余字段，方便前端展示
    private String buildingCode;
    private String buildingName;
    private Integer totalFloors;
    private Integer totalUnits;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}