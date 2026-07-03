package com.example.spms.service;

import com.example.spms.model.bo.RoleAssignPermissionRequest;
import com.example.spms.model.bo.RoleSaveRequest;
import com.example.spms.model.bo.RoleUpdateRequest;
import com.example.spms.model.po.SysRoleInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author poker
* @description 针对表【sys_role_info(角色信息表)】的数据库操作Service
* @createDate 2026-07-03 15:19:38
*/
public interface SysRoleInfoService extends IService<SysRoleInfo> {

    List<SysRoleInfo> listAll();

    void saveRole(RoleSaveRequest request);

    void updateRole(RoleUpdateRequest request);

    void assignPermissions(RoleAssignPermissionRequest request);

    List<Long> getRolePermissionIds(Long roleId);
}
