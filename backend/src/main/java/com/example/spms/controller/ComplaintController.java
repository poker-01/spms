package com.example.spms.controller;

import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.ComplaintQueryRequest;
import com.example.spms.model.bo.ComplaintReplyRequest;
import com.example.spms.model.vo.ComplaintDetailVO;
import com.example.spms.model.vo.ComplaintVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.ComplaintSuggestionService;
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

/**
 * 投诉建议管理控制器
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Tag(name = "投诉建议管理", description = "投诉建议CRUD、回复、关闭")
@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintSuggestionService complaintSuggestionService;

    @Operation(summary = "分页查询投诉建议")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('complaint:query')")
    public Result<Page<ComplaintVO>> page(ComplaintQueryRequest request) {
        return Result.success(complaintSuggestionService.pageComplaints(request));
    }

    @Operation(summary = "查询投诉建议详情")
    @GetMapping("/{complaintId}")
    @PreAuthorize("hasAuthority('complaint:query')")
    public Result<ComplaintDetailVO> detail(@PathVariable Long complaintId) {
        return Result.success(complaintSuggestionService.getComplaintDetail(complaintId));
    }

    @Operation(summary = "提交投诉建议（业主端）")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public Result<Void> submit(@AuthenticationPrincipal LoginUser loginUser,
                               @Valid @RequestBody ComplaintApplyRequest request) {
        complaintSuggestionService.submitComplaint(loginUser.getUserId(), request);
        return Result.success();
    }

    @Operation(summary = "回复投诉建议（物业端）")
    @PutMapping("/reply")
    @PreAuthorize("hasAuthority('complaint:reply')")
    public Result<Void> reply(@Valid @RequestBody ComplaintReplyRequest request) {
        complaintSuggestionService.replyComplaint(request);
        return Result.success();
    }

    @Operation(summary = "关闭投诉")
    @PutMapping("/{complaintId}/close")
    @PreAuthorize("hasAuthority('complaint:close')")
    public Result<Void> close(@PathVariable Long complaintId) {
        complaintSuggestionService.closeComplaint(complaintId);
        return Result.success();
    }

    @Operation(summary = "统计各状态投诉数量")
    @GetMapping("/stats/status")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<Object> countByStatus() {
        return Result.success(complaintSuggestionService.countByStatus());
    }

    @Operation(summary = "统计各类投诉数量")
    @GetMapping("/stats/type")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<Object> countByType() {
        return Result.success(complaintSuggestionService.countByType());
    }
}