package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName building_info
 */
@TableName(value ="building_info")
@Data
public class BuildingInfo implements Serializable {
    private Long id;
    private Long communityId;
    private String buildingCode;
    private String buildingName;
    private Integer totalFloors;
    private Integer totalUnits;
    private Integer status;
    private Integer isDeleted;
    private Integer version;
    private Date createTime;
    private Date updateTime;
    private Long createUser;
    private Long updateUser;

    // ========== 扩展字段（非数据库字段，用于关联查询） ==========
    @TableField(exist = false)
    private String communityName;       // 小区名称

    @TableField(exist = false)
    private Integer houseCount;         // 房屋数量

    @TableField(exist = false)
    private Integer vacantCount;        // 空置房屋数量

    @TableField(exist = false)
    private Integer occupiedCount;      // 已入住房屋数量

    private static final long serialVersionUID = 1L;
}