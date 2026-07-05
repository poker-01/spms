package com.example.spms.model.bo;

import lombok.Data;

/**
 * 费用项目查询请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class FeeItemQueryRequest {

    private String itemCode;

    private String itemName;

    private Integer itemType;

    private Integer status;

    private Integer page = 1;

    private Integer size = 10;
}