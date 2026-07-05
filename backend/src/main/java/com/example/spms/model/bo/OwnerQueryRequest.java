package com.example.spms.model.bo;

import lombok.Data;

/**
 * 业主分页查询请求
 */
@Data
public class OwnerQueryRequest {

    private String ownerName;
    private String ownerPhone;
    private Integer status;
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}