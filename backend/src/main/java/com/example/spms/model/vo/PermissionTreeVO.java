package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 权限树视图
 */
@Data
@Builder
public class PermissionTreeVO {

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

    private List<PermissionTreeVO> children;
}
