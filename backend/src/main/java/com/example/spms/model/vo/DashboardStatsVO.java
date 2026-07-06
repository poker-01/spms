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

    // ========== 系统统计 ==========
    private Long totalUsers;
    private Long totalRoles;

    // ========== 小区/楼栋/房屋统计 ==========
    private Long totalCommunities;
    private Long totalBuildings;
    private Long totalHouses;
    private Long occupiedHouses;

    // ========== 业主统计 ==========
    private Long totalOwners;

    // ========== 报修统计 ==========
    private Long totalRepairs;
    private Long pendingRepairs;
    private Long processingRepairs;
    private Long completedRepairs;

    // ========== 投诉统计 ==========
    private Long totalComplaints;
    private Long pendingComplaints;

    // ========== 财务统计 ==========
    private Long totalBills;
    private Long unpaidBills;
    private Long paidBills;
    private Long overdueBills;
    private BigDecimal totalPaymentAmount;
    private BigDecimal totalOverdueAmount;

    // ========== 分布/趋势 ==========
    /** 近7日报修趋势 */
    private List<DailyRepairStat> dailyRepairStats;

    /** 报修类型分布 */
    private Map<String, Long> repairTypeDistribution;

    /** 投诉类型分布 */
    private Map<String, Long> complaintTypeDistribution;

    /** 账单状态分布 */
    private Map<String, Long> billStatusDistribution;

    /** 平均处理时长（小时） */
    private BigDecimal avgProcessHours;

    @Data
    @Builder
    public static class DailyRepairStat {
        private String date;
        private Long total;
        private Long completed;
    }
}
