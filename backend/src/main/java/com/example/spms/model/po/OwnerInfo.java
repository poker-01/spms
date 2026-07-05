package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @TableName owner_info
 */
@TableName(value ="owner_info")
@Data
public class OwnerInfo implements Serializable {
    private Long id;
    private String ownerName;
    private String ownerPhone;
    private String idCard;
    private Integer gender;
    private String email;
    private Integer status;
    private Integer isDeleted;
    private Integer version;
    private Date createTime;
    private Date updateTime;
    private Long createUser;
    private Long updateUser;

    // ========== 扩展字段（非数据库字段，用于关联查询） ==========
    @TableField(exist = false)
    private List<Map<String, Object>> houses;   // 关联房屋列表

    private static final long serialVersionUID = 1L;
}