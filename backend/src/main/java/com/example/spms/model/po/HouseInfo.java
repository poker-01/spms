package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName house_info
 */
@TableName(value ="house_info")
@Data
public class HouseInfo implements Serializable {
    private Long id;
    private Long buildingId;
    private String houseNumber;
    private Integer floorNumber;
    private BigDecimal houseArea;
    private String houseType;
    private String ownerName;
    private String ownerPhone;
    private Integer status;
    private Integer isDeleted;
    private Integer version;
    private Date createTime;
    private Date updateTime;
    private Long createUser;
    private Long updateUser;

    // ========== 扩展字段（非数据库字段，用于关联查询） ==========
    @TableField(exist = false)
    private String buildingName;        // 楼栋名称

    @TableField(exist = false)
    private String communityName;       // 小区名称

    @TableField(exist = false)
    private Long communityId;           // 小区ID

    private static final long serialVersionUID = 1L;
}