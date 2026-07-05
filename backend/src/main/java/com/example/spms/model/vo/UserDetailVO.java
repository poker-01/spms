package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户详情视图
 */
@Data
@Builder
public class UserDetailVO {

    private Long id;

    private String userName;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String avatarAddress;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    /** 角色ID列表 */
    private List<Long> roleIds;

    /** 角色名称列表 */
    private List<String> roleNames;
}
