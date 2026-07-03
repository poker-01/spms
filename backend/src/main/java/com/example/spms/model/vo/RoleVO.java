package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 角色视图
 */
@Data
@Builder
public class RoleVO {

    private Long id;

    private String roleCode;

    private String roleName;
}
