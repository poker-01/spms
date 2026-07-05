package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 巡检记录保存请求（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class InspectionSaveRequest {

    @NotBlank(message = "巡检地点不能为空")
    private String location;

    @NotNull(message = "巡检类型不能为空")
    private Integer inspectionType;

    private String inspectionDesc;

    @NotNull(message = "巡检结果不能为空")
    private Integer result;

    private String resultDesc;

    private String attachments;

    private Date inspectionTime;
}