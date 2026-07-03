package com.example.spms.controller;

import com.example.spms.common.Result;
import com.example.spms.model.bo.RoleAssignPermissionRequest;
import com.example.spms.model.bo.RoleSaveRequest;
import com.example.spms.model.bo.RoleUpdateRequest;
import com.example.spms.model.po.SysRoleInfo;
import com.example.spms.service.SysRoleInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理", description = "系统角色 CRUD 与权限分配")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleInfoService sysRoleInfoService;

    @Operation(summary = "查询全部角色")
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<SysRoleInfo>> list() {
        return Result.success(sysRoleInfoService.listAll());
    }

    @Operation(summary = "新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Void> save(@Valid @RequestBody RoleSaveRequest request) {
        sysRoleInfoService.saveRole(request);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> update(@Valid @RequestBody RoleUpdateRequest request) {
        sysRoleInfoService.updateRole(request);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleInfoService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "查询角色已分配权限")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        return Result.success(sysRoleInfoService.getRolePermissionIds(roleId));
    }

    @Operation(summary = "角色权限分配")
    @PostMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('system:role:assign')")
    public Result<Void> assignPermissions(@Valid @RequestBody RoleAssignPermissionRequest request) {
        sysRoleInfoService.assignPermissions(request);
        return Result.success();
    }
}
