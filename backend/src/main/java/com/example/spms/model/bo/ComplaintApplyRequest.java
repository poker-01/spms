package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 投诉建议申请请求
 */
@Data
public class ComplaintApplyRequest {

    @NotNull(message = "类型不能为空")
    private Integer type;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String contactPhone;
}
