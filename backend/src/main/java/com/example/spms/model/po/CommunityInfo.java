package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName community_info
 */
@TableName(value ="community_info")
@Data
public class CommunityInfo implements Serializable {
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
    private String managerName;
    private String managerPhone;
    private Integer status;
    private Integer isDeleted;
    private Integer version;
    private Date createTime;
    private Date updateTime;
    private Long createUser;
    private Long updateUser;

    // ========== 扩展字段（非数据库字段，用于关联查询） ==========
    @TableField(exist = false)
    private Integer buildingCount;      // 楼栋数量

    @TableField(exist = false)
    private Integer houseCount;         // 房屋数量

    @TableField(exist = false)
    private Integer ownerCount;         // 业主数量

    private static final long serialVersionUID = 1L;
}