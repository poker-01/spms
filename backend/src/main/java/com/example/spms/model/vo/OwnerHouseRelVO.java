package com.example.spms.model.vo;

import lombok.Data;

/**
 * 业主房屋关联响应
 */
@Data
public class OwnerHouseRelVO {

    private Long id;
    private Long ownerInfoId;
    private Long houseInfoId;
    private String houseFullName;  // 完整房屋名称（小区+楼栋+房屋）
    private Integer relationType;
    private Integer isPrimary;
}