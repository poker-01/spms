package com.example.spms.controller;

import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.InspectionQueryRequest;
import com.example.spms.model.bo.InspectionSaveRequest;
import com.example.spms.model.vo.InspectionVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.InspectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 巡检记录控制器（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Tag(name = "巡检管理（选做）", description = "巡检记录管理")
@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @Operation(summary = "分页查询巡检记录")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('inspection:query')")
    public Result<Page<InspectionVO>> page(InspectionQueryRequest request) {
        return Result.success(inspectionService.pageInspections(request));
    }

    @Operation(summary = "新增巡检记录")
    @PostMapping
    @PreAuthorize("hasAuthority('inspection:add')")
    public Result<Void> save(@AuthenticationPrincipal LoginUser loginUser,
                             @Valid @RequestBody InspectionSaveRequest request) {
        inspectionService.saveInspection(loginUser.getUserId(), request);
        return Result.success();
    }
}