package com.example.spms.model.bo;

import lombok.Data;

/**
 * 小区分页查询请求
 */
@Data
public class CommunityQueryRequest {

    private String communityName;
    private String city;
    private Integer status;
    private long pageNum = 1;
    private long pageSize = 10;
}