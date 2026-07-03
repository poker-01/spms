package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增权限（菜单/按钮）请求
 */
@Data
public class PermissionSaveRequest {

    private Long parentId;

    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    private String permissionIcon;

    private String permissionPath;

    private String permissionComponent;

    private String permissionStr;

    private Integer sortOrder;

    private Integer visible;
}
