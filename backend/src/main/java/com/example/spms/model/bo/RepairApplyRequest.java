package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修申请请求
 */
@Data
public class RepairApplyRequest {

    @NotBlank(message = "报修内容不能为空")
    private String content;

    @NotNull(message = "报修类型不能为空")
    private Integer repairType;

    private String contactPhone;
}