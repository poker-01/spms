package com.example.spms.controller;

import com.example.spms.common.Result;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.vo.BillDetailVO;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerRepairVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.OwnerService;
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
import java.util.Map;

/**
 * 业主端控制器
 *
 * @Author SPMS
 * @Date 2026/07/03
 */
@Tag(name = "业主端", description = "业主首页、账单、报修、投诉建议")
@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @Operation(summary = "业主首页概览")
    @GetMapping("/home")
    @PreAuthorize("hasAuthority('owner:bill:query')")
    public Result<OwnerHomeVO> home(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(ownerService.getHomeData(loginUser.getUserId()));
    }

    @Operation(summary = "我的账单列表")
    @GetMapping("/bills")
    @PreAuthorize("hasAuthority('owner:bill:query')")
    public Result<List<OwnerBillVO>> bills(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(ownerService.listBills(loginUser.getUserId()));
    }

    @Operation(summary = "我的账单详情")
    @GetMapping("/bills/{id}")
    @PreAuthorize("hasAuthority('owner:bill:query')")
    public Result<BillDetailVO> billDetail(@AuthenticationPrincipal LoginUser loginUser,
                                           @PathVariable Long id) {
        return Result.success(ownerService.getBillDetail(loginUser.getUserId(), id));
    }

    @Operation(summary = "业主缴纳账单")
    @PostMapping("/bills/{id}/pay")
    @PreAuthorize("hasAuthority('owner:bill:pay')")
    public Result<Void> payBill(@AuthenticationPrincipal LoginUser loginUser,
                                @PathVariable Long id,
                                @RequestBody Map<String, Integer> request) {
        ownerService.payBill(loginUser.getUserId(), id, request.get("payMethod"));
        return Result.success();
    }

    @Operation(summary = "我的报修列表")
    @GetMapping("/repairs")
    @PreAuthorize("hasAuthority('owner:repair:query')")
    public Result<List<OwnerRepairVO>> repairs(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(ownerService.listRepairs(loginUser.getUserId()));
    }

    @Operation(summary = "我的报修详情")
    @GetMapping("/repairs/{orderId}")
    @PreAuthorize("hasAuthority('owner:repair:query')")
    public Result<OwnerRepairVO> repairDetail(@AuthenticationPrincipal LoginUser loginUser,
                                              @PathVariable Long orderId) {
        return Result.success(ownerService.getRepairDetail(loginUser.getUserId(), orderId));
    }

    @Operation(summary = "提交报修申请")
    @PostMapping("/repairs")
    @PreAuthorize("hasAuthority('owner:repair:add')")
    public Result<Void> applyRepair(@AuthenticationPrincipal LoginUser loginUser,
                                    @Valid @RequestBody RepairApplyRequest request) {
        ownerService.applyRepair(loginUser.getUserId(), request);
        return Result.success();
    }

    @Operation(summary = "我的投诉建议列表")
    @GetMapping("/complaints")
    @PreAuthorize("hasAuthority('owner:complaint:query')")
    public Result<List<OwnerComplaintVO>> complaints(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(ownerService.listComplaints(loginUser.getUserId()));
    }

    @Operation(summary = "我的投诉建议详情")
    @GetMapping("/complaints/{id}")
    @PreAuthorize("hasAuthority('owner:complaint:query')")
    public Result<OwnerComplaintVO> complaintDetail(@AuthenticationPrincipal LoginUser loginUser,
                                                    @PathVariable Long id) {
        return Result.success(ownerService.getComplaintDetail(loginUser.getUserId(), id));
    }

    @Operation(summary = "提交投诉建议")
    @PostMapping("/complaints")
    @PreAuthorize("hasAuthority('owner:complaint:add')")
    public Result<Void> applyComplaint(@AuthenticationPrincipal LoginUser loginUser,
                                       @Valid @RequestBody ComplaintApplyRequest request) {
        ownerService.applyComplaint(loginUser.getUserId(), request);
        return Result.success();
    }

    @Operation(summary = "取消投诉建议")
    @PutMapping("/complaints/{id}/cancel")
    @PreAuthorize("hasAuthority('owner:complaint:add')")
    public Result<Void> cancelComplaint(@AuthenticationPrincipal LoginUser loginUser,
                                        @PathVariable Long id) {
        ownerService.cancelComplaint(loginUser.getUserId(), id);
        return Result.success();
    }
}
