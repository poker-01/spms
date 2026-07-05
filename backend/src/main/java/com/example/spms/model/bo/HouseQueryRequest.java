package com.example.spms.model.bo;

import lombok.Data;

/**
 * 房屋分页查询请求
 */
@Data
public class HouseQueryRequest {

    private Long buildingId;
    private Long ownerId;
    private String houseNumber;
    private Integer status;
    private long pageNum = 1;
    private long pageSize = 10;
}