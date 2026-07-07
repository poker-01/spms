package com.example.spms.controller;

import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.*;
import com.example.spms.model.vo.BillDetailVO;
import com.example.spms.model.vo.BillVO;
import com.example.spms.model.vo.FeeItemVO;
import com.example.spms.model.vo.PaymentRecordVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.BillInfoService;
import com.example.spms.service.FeeItemService;
import com.example.spms.service.PaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务管理控制器
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Tag(name = "财务管理", description = "费用项目、账单、缴费记录管理")
@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FeeItemService feeItemService;
    private final BillInfoService billInfoService;
    private final PaymentRecordService paymentRecordService;

    // ==================== 费用项目管理 ====================

    @Operation(summary = "分页查询费用项目")
    @GetMapping("/fee-items/page")
    @PreAuthorize("hasAuthority('finance:fee:query')")
    public Result<Page<FeeItemVO>> pageFeeItems(FeeItemQueryRequest request) {
        return Result.success(feeItemService.pageFeeItems(request));
    }

    @Operation(summary = "查询所有启用的费用项目")
    @GetMapping("/fee-items/enabled")
    @PreAuthorize("hasAnyAuthority('finance:fee:query', 'finance:bill:generate')")
    public Result<List<com.example.spms.model.po.FeeItem>> listEnabledFeeItems() {
        return Result.success(feeItemService.listEnabled());
    }

    @Operation(summary = "新增费用项目")
    @PostMapping("/fee-items")
    @PreAuthorize("hasAuthority('finance:fee:add')")
    public Result<Void> saveFeeItem(@Valid @RequestBody FeeItemSaveRequest request) {
        feeItemService.saveFeeItem(request);
        return Result.success();
    }

    @Operation(summary = "修改费用项目")
    @PutMapping("/fee-items")
    @PreAuthorize("hasAuthority('finance:fee:edit')")
    public Result<Void> updateFeeItem(@Valid @RequestBody FeeItemUpdateRequest request) {
        feeItemService.updateFeeItem(request);
        return Result.success();
    }

    @Operation(summary = "删除费用项目")
    @DeleteMapping("/fee-items/{id}")
    @PreAuthorize("hasAuthority('finance:fee:delete')")
    public Result<Void> deleteFeeItem(@PathVariable Long id) {
        feeItemService.removeById(id);
        return Result.success();
    }

    // ==================== 账单管理 ====================

    @Operation(summary = "分页查询账单")
    @GetMapping("/bills/page")
    @PreAuthorize("hasAuthority('finance:bill:query')")
    public Result<Page<BillVO>> pageBills(@AuthenticationPrincipal LoginUser loginUser,
                                          BillQueryRequest request) {
        // 小区管理员数据隔离：非超级管理员只能查看本小区账单
        request.setCommunityId(com.example.spms.common.CommunityFilterHelper.getCommunityId(loginUser));
        return Result.success(billInfoService.pageBills(request));
    }

    @Operation(summary = "查询账单详情")
    @GetMapping("/bills/{billId}")
    @PreAuthorize("hasAuthority('finance:bill:query')")
    public Result<BillDetailVO> getBillDetail(@PathVariable Long billId) {
        return Result.success(billInfoService.getBillDetail(billId));
    }

    @Operation(summary = "生成账单")
    @PostMapping("/bills/generate")
    @PreAuthorize("hasAuthority('finance:bill:generate')")
    public Result<Void> generateBills(@Valid @RequestBody BillGenerateRequest request) {
        billInfoService.generateBills(request);
        return Result.success();
    }

    @Operation(summary = "一键生成月度账单（为当前小区所有业主自动生成当月账单）")
    @PostMapping("/bills/auto-generate")
    @PreAuthorize("hasAuthority('finance:bill:generate')")
    public Result<Integer> autoGenerateMonthlyBills(@AuthenticationPrincipal LoginUser loginUser,
                                                    @RequestParam(required = false) String billPeriod) {
        if (billPeriod == null || billPeriod.isBlank()) {
            billPeriod = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        Long communityId = com.example.spms.common.CommunityFilterHelper.getCommunityId(loginUser);
        int count = billInfoService.autoGenerateMonthlyBills(billPeriod, communityId);
        return Result.success(count);
    }

    @Operation(summary = "缴费登记")
    @PostMapping("/bills/pay")
    @PreAuthorize("hasAuthority('finance:bill:pay')")
    public Result<Void> payBill(@AuthenticationPrincipal LoginUser loginUser,
                                @Valid @RequestBody BillPayRequest request) {
        billInfoService.payBill(loginUser.getUserId(), request);
        return Result.success();
    }

    @Operation(summary = "删除账单")
    @DeleteMapping("/bills/{id}")
    @PreAuthorize("hasAuthority('finance:bill:delete')")
    public Result<Void> deleteBill(@PathVariable Long id) {
        billInfoService.deleteBill(id);
        return Result.success();
    }

    @Operation(summary = "统计各状态账单数量")
    @GetMapping("/bills/stats/status")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<Object> countBillByStatus(@AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = com.example.spms.common.CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(billInfoService.countByStatus(communityId));
    }

    @Operation(summary = "统计总欠费金额")
    @GetMapping("/bills/stats/overdue")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<java.math.BigDecimal> sumOverdueAmount(@AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = com.example.spms.common.CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(billInfoService.sumOverdueAmount(communityId));
    }

    // ==================== 缴费记录管理 ====================

    @Operation(summary = "分页查询缴费记录")
    @GetMapping("/payment-records/page")
    @PreAuthorize("hasAuthority('finance:payment:query')")
    public Result<Page<PaymentRecordVO>> pagePaymentRecords(PaymentRecordQueryRequest request) {
        return Result.success(paymentRecordService.pagePaymentRecords(request));
    }

    @Operation(summary = "统计总缴费金额")
    @GetMapping("/payment-records/stats/total")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<java.math.BigDecimal> sumTotalPayAmount() {
        return Result.success(paymentRecordService.sumTotalPayAmount());
    }
}