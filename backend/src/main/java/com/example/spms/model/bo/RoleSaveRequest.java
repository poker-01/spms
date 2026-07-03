package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增角色请求
 */
@Data
public class RoleSaveRequest {

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;
}
