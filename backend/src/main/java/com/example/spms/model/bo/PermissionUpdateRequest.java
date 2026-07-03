package com.example.spms.model.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改权限（菜单/按钮）请求
 */
@Data
public class PermissionUpdateRequest {

    @NotNull(message = "权限ID不能为空")
    private Long id;

    private Long parentId;

    private String permissionCode;

    private String permissionName;

    private Integer permissionType;

    private String permissionIcon;

    private String permissionPath;

    private String permissionComponent;

    private String permissionStr;

    private Integer sortOrder;

    private Integer visible;
}
