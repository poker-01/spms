package com.example.spms.controller;

import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.UserAssignRoleRequest;
import com.example.spms.model.bo.UserQueryRequest;
import com.example.spms.model.bo.UserSaveRequest;
import com.example.spms.model.bo.UserStatusRequest;
import com.example.spms.model.bo.UserUpdateRequest;
import com.example.spms.model.vo.UserPageVO;
import com.example.spms.service.SysUserInfoService;
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
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "系统用户 CRUD 与角色分配")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserInfoService sysUserInfoService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<Page<UserPageVO>> page(UserQueryRequest request) {
        return Result.success(sysUserInfoService.pageUsers(request));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Void> save(@Valid @RequestBody UserSaveRequest request) {
        sysUserInfoService.saveUser(request);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> update(@Valid @RequestBody UserUpdateRequest request) {
        sysUserInfoService.updateUser(request);
        return Result.success();
    }

    @Operation(summary = "切换用户状态")
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> toggleStatus(@Valid @RequestBody UserStatusRequest request) {
        sysUserInfoService.toggleStatus(request);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> resetPassword(@PathVariable Long userId) {
        sysUserInfoService.resetPassword(userId);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserInfoService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "查询用户已分配角色")
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<List<Long>> getUserRoles(@PathVariable Long userId) {
        return Result.success(sysUserInfoService.getUserRoleIds(userId));
    }

    @Operation(summary = "用户角色分配")
    @PostMapping("/assign-roles")
    @PreAuthorize("hasAuthority('system:user:assign')")
    public Result<Void> assignRoles(@Valid @RequestBody UserAssignRoleRequest request) {
        sysUserInfoService.assignRoles(request);
        return Result.success();
    }
}
