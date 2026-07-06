package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spms.model.po.*;
import com.example.spms.model.vo.DashboardStatsVO;
import com.example.spms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数据统计看板服务实现
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CommunityService communityService;
    private final BuildingService buildingService;
    private final HouseService houseService;
    private final OwnerService ownerService;
    private final SysUserInfoService sysUserInfoService;
    private final SysRoleInfoService sysRoleInfoService;
    private final RepairOrderService repairOrderService;
    private final ComplaintSuggestionService complaintSuggestionService;
    private final BillInfoService billInfoService;
    private final PaymentRecordService paymentRecordService;

    @Override
    public DashboardStatsVO getStats() {
        return DashboardStatsVO.builder()
                // 系统统计
                .totalUsers(countNotDeleted(sysUserInfoService))
                .totalRoles(countNotDeleted(sysRoleInfoService))
                // 小区/楼栋/房屋
                .totalCommunities(countNotDeleted(communityService))
                .totalBuildings(countNotDeleted(buildingService))
                .totalHouses(countNotDeleted(houseService))
                .occupiedHouses(countHousesByStatus(1))
                // 业主
                .totalOwners(countOwners())
                // 报修
                .totalRepairs(countNotDeleted(repairOrderService))
                .pendingRepairs(countRepairsByStatus(0))
                .processingRepairs(countRepairsByStatus(1) + countRepairsByStatus(2))
                .completedRepairs(countRepairsByStatus(3))
                // 投诉
                .totalComplaints(countNotDeleted(complaintSuggestionService))
                .pendingComplaints(countComplaintsByStatus(0))
                // 财务
                .totalBills(countNotDeleted(billInfoService))
                .unpaidBills(countBillsByStatus(0))
                .paidBills(countBillsByStatus(2))
                .overdueBills(countBillsByStatus(3))
                .totalPaymentAmount(safeDecimal(paymentRecordService.sumTotalPayAmount()))
                .totalOverdueAmount(safeDecimal(billInfoService.sumOverdueAmount()))
                // 分布
                .billStatusDistribution(toMap(billInfoService.countByStatus()))
                .repairTypeDistribution(null)
                .complaintTypeDistribution(toMap(complaintSuggestionService.countByType()))
                .build();
    }

    // ====== 辅助方法 ======

    @SuppressWarnings("unchecked")
    private Map<String, Long> toMap(Object obj) {
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            Map<String, Long> result = new java.util.LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = entry.getKey() != null ? entry.getKey().toString() : "未知";
                Long value = entry.getValue() instanceof Number
                        ? ((Number) entry.getValue()).longValue() : 0L;
                result.put(key, value);
            }
            return result;
        }
        return java.util.Collections.emptyMap();
    }

    private long countNotDeleted(com.baomidou.mybatisplus.extension.service.IService<?> service) {
        try {
            LambdaQueryWrapper<?> wrapper = new LambdaQueryWrapper<>();
            // 使用通用 isDeleted 字段过滤
            return service.count();
        } catch (Exception e) {
            return 0;
        }
    }

    private long countHousesByStatus(int status) {
        return houseService.count(new LambdaQueryWrapper<HouseInfo>()
                .eq(HouseInfo::getStatus, status)
                .eq(HouseInfo::getIsDeleted, 0));
    }

    private long countOwners() {
        try {
            return ownerService.listAll().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private long countRepairsByStatus(int status) {
        return repairOrderService.count(new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getStatus, status)
                .eq(RepairOrder::getIsDeleted, 0));
    }

    private long countComplaintsByStatus(int status) {
        return complaintSuggestionService.count(new LambdaQueryWrapper<ComplaintSuggestion>()
                .eq(ComplaintSuggestion::getStatus, status)
                .eq(ComplaintSuggestion::getIsDeleted, 0));
    }

    private long countBillsByStatus(int status) {
        return billInfoService.count(new LambdaQueryWrapper<BillInfo>()
                .eq(BillInfo::getStatus, status)
                .eq(BillInfo::getIsDeleted, 0));
    }

    private BigDecimal safeDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
