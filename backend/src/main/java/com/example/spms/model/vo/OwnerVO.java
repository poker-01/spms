package com.example.spms.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * 业主信息响应
 */
@Data
public class OwnerVO {

    private Long id;
    private String ownerName;
    private String ownerPhone;
    private String idCard;
    private Integer gender;
    private String email;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}