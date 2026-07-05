package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增楼栋请求
 */
@Data
public class BuildingSaveRequest {

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotBlank(message = "楼栋编码不能为空")
    private String buildingCode;

    @NotBlank(message = "楼栋名称不能为空")
    private String buildingName;

    private Integer totalFloors;
    private Integer totalUnits;
    private Integer status;
}