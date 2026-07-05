package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据统计看板VO
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class DashboardStatsVO {

    // 报修统计
    private Long totalRepairs;

    private Long pendingRepairs;

    private Long processingRepairs;

    private Long completedRepairs;

    // 投诉统计
    private Long totalComplaints;

    private Long pendingComplaints;

    // 近7日报修趋势
    private List<DailyRepairStat> dailyRepairStats;

    // 报修类型分布
    private Map<String, Long> repairTypeDistribution;

    // 投诉类型分布
    private Map<String, Long> complaintTypeDistribution;

    // 平均处理时长（小时）
    private BigDecimal avgProcessHours;

    @Data
    @Builder
    public static class DailyRepairStat {
        private String date;
        private Long total;
        private Long completed;
    }
}