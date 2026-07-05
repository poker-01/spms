package com.example.spms.model.bo;

import lombok.Data;

/**
 * 楼栋分页查询请求
 */
@Data
public class BuildingQueryRequest {

    private Long communityId;
    private String buildingName;
    private Integer status;
    private long pageNum = 1;
    private long pageSize = 10;
}