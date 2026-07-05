package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改楼栋请求
 */
@Data
public class BuildingUpdateRequest {

    @NotNull(message = "楼栋ID不能为空")
    private Long id;

    private Long communityId;
    private String buildingCode;
    private String buildingName;
    private Integer totalFloors;
    private Integer totalUnits;
    private Integer status;
}