package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户分页视图
 */
@Data
@Builder
public class UserPageVO {

    private Long id;

    private String userName;

    private String fullName;

    private String phoneNumber;

    private String email;

    private Integer status;

    private Date createTime;

    private List<String> roleNames;

    /** 所属小区ID */
    private Long communityId;

    /** 所属小区名称 */
    private String communityName;
}
