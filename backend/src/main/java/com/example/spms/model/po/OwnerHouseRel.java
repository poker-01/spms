package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName owner_house_rel
 */
@TableName(value ="owner_house_rel")
@Data
public class OwnerHouseRel implements Serializable {
    private Long id;
    private Long ownerInfoId;
    private Long houseInfoId;
    private Integer relationType;
    private Integer isPrimary;
    private Date moveInDate;
    private Integer isDeleted;
    private Integer version;
    private Date createTime;
    private Date updateTime;
    private Long createUser;
    private Long updateUser;

    // ========== 扩展字段（非数据库字段，用于关联查询） ==========
    @TableField(exist = false)
    private String ownerName;           // 业主姓名

    @TableField(exist = false)
    private String ownerPhone;          // 业主电话

    @TableField(exist = false)
    private String houseNumber;         // 房号

    @TableField(exist = false)
    private String buildingName;        // 楼栋名称

    @TableField(exist = false)
    private String communityName;       // 小区名称

    private static final long serialVersionUID = 1L;
}