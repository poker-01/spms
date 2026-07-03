package com.example.spms.controller;

import com.example.spms.common.Result;
import com.example.spms.model.bo.PermissionSaveRequest;
import com.example.spms.model.bo.PermissionUpdateRequest;
import com.example.spms.model.vo.PermissionTreeVO;
import com.example.spms.service.SysPermissionInfoService;
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
 * 菜单权限管理控制器
 */
@Tag(name = "菜单权限管理", description = "菜单与按钮权限维护")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final SysPermissionInfoService sysPermissionInfoService;

    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<PermissionTreeVO>> tree() {
        return Result.success(sysPermissionInfoService.tree());
    }

    @Operation(summary = "新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    public Result<Void> save(@Valid @RequestBody PermissionSaveRequest request) {
        sysPermissionInfoService.savePermission(request);
        return Result.success();
    }

    @Operation(summary = "修改权限")
    @PutMapping
    @PreAuthorize("hasAuthority('system:permission:edit')")
    public Result<Void> update(@Valid @RequestBody PermissionUpdateRequest request) {
        sysPermissionInfoService.updatePermission(request);
        return Result.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysPermissionInfoService.removeById(id);
        return Result.success();
    }
}
