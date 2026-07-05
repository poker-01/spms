package com.example.spms.model.bo;

import lombok.Data;

/**
 * 用户分页查询请求
 */
@Data
public class UserQueryRequest {

    private String userName;

    private String fullName;

    private Integer status;

    /** 角色ID（按角色筛选） */
    private Long roleId;

    private long pageNum = 1;

    private long pageSize = 10;
}
