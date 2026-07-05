package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 巡检记录PO（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@TableName("inspection_record")
public class InspectionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String inspectionNo;

    private String location;

    private Integer inspectionType;

    private String inspectionDesc;

    private Integer result;

    private String resultDesc;

    private String attachments;

    private Long inspectorId;

    private Date inspectionTime;

    @TableLogic
    private Integer isDeleted;

    @Version
    private Integer version;

    private Date createTime;

    private Date updateTime;

    private Long createUser;

    private Long updateUser;
}