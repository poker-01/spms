package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改房屋请求
 */
@Data
public class HouseUpdateRequest {

    @NotNull(message = "房屋ID不能为空")
    private Long id;

    private Long buildingId;
    private String houseNumber;
    private Integer floorNumber;
    private BigDecimal houseArea;
    private String houseType;
    private String ownerName;
    private String ownerPhone;
    private Integer status;
}