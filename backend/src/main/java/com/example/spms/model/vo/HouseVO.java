package com.example.spms.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋响应对象
 */
@Data
public class HouseVO {

    private Long id;
    private Long buildingId;
    private String buildingName;  // 冗余字段
    private String communityName; // 冗余字段
    private String houseNumber;
    private Integer floorNumber;
    private BigDecimal houseArea;
    private String houseType;
    private String ownerName;
    private String ownerPhone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}