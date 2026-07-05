package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改业主请求
 */
@Data
public class OwnerUpdateRequest {

    @NotNull(message = "业主ID不能为空")
    private Long id;

    private String ownerName;
    private String ownerPhone;
    private String idCard;
    private Integer gender;
    private String email;
    private Integer status;
}