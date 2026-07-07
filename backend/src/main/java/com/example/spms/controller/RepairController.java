package com.example.spms.controller;

import com.example.spms.common.CommunityFilterHelper;
import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.RepairAssignRequest;
import com.example.spms.model.bo.RepairCompleteRequest;
import com.example.spms.model.bo.RepairEvaluateRequest;
import com.example.spms.model.bo.RepairQueryRequest;
import com.example.spms.model.vo.RepairDetailVO;
import com.example.spms.model.vo.RepairOrderVO;
import com.example.spms.security.LoginUser;
import com.example.spms.model.vo.UserPageVO;
import com.example.spms.service.RepairOrderService;
import com.example.spms.service.SysUserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报修工单管理控制器
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Tag(name = "报修工单管理", description = "报修工单CRUD、派单、处理、完成、评价")
@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final RepairOrderService repairOrderService;
    private final SysUserInfoService sysUserInfoService;

    @Operation(summary = "获取维修人员列表（用于派单下拉选择，仅返回当前小区维修人员）")
    @GetMapping("/repairers")
    @PreAuthorize("hasAuthority('repair:assign')")
    public Result<List<UserPageVO>> listRepairers(@AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(sysUserInfoService.listByRoleIdAndCommunityId(4L, communityId));
    }

    @Operation(summary = "分页查询报修工单")
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('repair:query', 'ROLE_REPAIR')")
    public Result<Page<RepairOrderVO>> page(RepairQueryRequest request,
                                             @AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(repairOrderService.pageRepairs(request, communityId));
    }

    @Operation(summary = "查询报修工单详情")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyAuthority('repair:query', 'ROLE_REPAIR')")
    public Result<RepairDetailVO> detail(@PathVariable Long orderId) {
        return Result.success(repairOrderService.getRepairDetail(orderId));
    }

    @Operation(summary = "派单")
    @PutMapping("/assign")
    @PreAuthorize("hasAuthority('repair:assign')")
    public Result<Void> assign(@Valid @RequestBody RepairAssignRequest request) {
        repairOrderService.assignRepair(request);
        return Result.success();
    }

    @Operation(summary = "开始处理")
    @PutMapping("/{orderId}/start")
    @PreAuthorize("hasAnyAuthority('repair:process', 'ROLE_REPAIR')")
    public Result<Void> startProcess(@PathVariable Long orderId) {
        repairOrderService.startProcess(orderId);
        return Result.success();
    }

    @Operation(summary = "完成维修")
    @PutMapping("/complete")
    @PreAuthorize("hasAnyAuthority('repair:process', 'ROLE_REPAIR')")
    public Result<Void> complete(@Valid @RequestBody RepairCompleteRequest request) {
        repairOrderService.completeRepair(request);
        return Result.success();
    }

    @Operation(summary = "评价工单")
    @PutMapping("/evaluate")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public Result<Void> evaluate(@Valid @RequestBody RepairEvaluateRequest request) {
        repairOrderService.evaluateRepair(request);
        return Result.success();
    }

    @Operation(summary = "取消工单")
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyAuthority('repair:cancel', 'ROLE_OWNER')")
    public Result<Void> cancel(@PathVariable Long orderId) {
        repairOrderService.cancelRepair(orderId);
        return Result.success();
    }

    @Operation(summary = "统计各状态工单数量")
    @GetMapping("/stats/status")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<Object> countByStatus() {
        return Result.success(repairOrderService.countByStatus());
    }
}