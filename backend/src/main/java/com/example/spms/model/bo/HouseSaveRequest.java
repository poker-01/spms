package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增房屋请求
 */
@Data
public class HouseSaveRequest {

    @NotNull(message = "楼栋ID不能为空")
    private Long buildingId;

    @NotBlank(message = "房屋编号不能为空")
    private String houseNumber;

    private Integer floorNumber;
    private BigDecimal houseArea;
    private String houseType;
    private String ownerName;
    private String ownerPhone;
    private Integer status;
}