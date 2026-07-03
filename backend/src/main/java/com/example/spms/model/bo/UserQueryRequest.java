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

    private long pageNum = 1;

    private long pageSize = 10;
}
