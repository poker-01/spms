package com.example.spms.controller;

import com.example.spms.common.Result;
import com.example.spms.model.vo.DashboardStatsVO;
import com.example.spms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计看板控制器
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Tag(name = "数据统计看板", description = "全局统计数据")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取全局统计数据")
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<DashboardStatsVO> getStats() {
        return Result.success(dashboardService.getStats());
    }
}
