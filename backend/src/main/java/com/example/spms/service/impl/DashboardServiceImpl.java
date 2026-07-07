package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spms.model.po.*;
import com.example.spms.model.vo.DashboardStatsVO;
import com.example.spms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final OwnerHouseRelService ownerHouseRelService;
    private final SysUserInfoService sysUserInfoService;
    private final SysRoleInfoService sysRoleInfoService;
    private final RepairOrderService repairOrderService;
    private final ComplaintSuggestionService complaintSuggestionService;
    private final BillInfoService billInfoService;
    private final PaymentRecordService paymentRecordService;

    @Override
    public DashboardStatsVO getStats(Long communityId) {
        // 如果是非超级管理员（指定了小区），则统计该小区数据
        if (communityId != null) {
            return getCommunityStats(communityId);
        }
        return getGlobalStats();
    }

    private DashboardStatsVO getGlobalStats() {
        return DashboardStatsVO.builder()
                .totalUsers(countNotDeleted(sysUserInfoService))
                .totalRoles(countNotDeleted(sysRoleInfoService))
                .totalCommunities(countNotDeleted(communityService))
                .totalBuildings(countNotDeleted(buildingService))
                .totalHouses(countNotDeleted(houseService))
                .occupiedHouses(countOccupiedHouses())
                .totalOwners(countOwners())
                .totalRepairs(countNotDeleted(repairOrderService))
                .pendingRepairs(countRepairsByStatus(0))
                .processingRepairs(countRepairsByStatus(1) + countRepairsByStatus(2))
                .completedRepairs(countRepairsByStatus(3))
                .totalComplaints(countNotDeleted(complaintSuggestionService))
                .pendingComplaints(countComplaintsByStatus(0))
                .totalBills(countNotDeleted(billInfoService))
                .unpaidBills(countBillsByStatus(0))
                .paidBills(countBillsByStatus(2))
                .overdueBills(countBillsByStatus(3))
                .totalPaymentAmount(safeDecimal(paymentRecordService.sumTotalPayAmount()))
                .totalOverdueAmount(safeDecimal(billInfoService.sumOverdueAmount()))
                .billStatusDistribution(toMap(billInfoService.countByStatus()))
                .repairTypeDistribution(null)
                .complaintTypeDistribution(toMap(complaintSuggestionService.countByType()))
                .build();
    }

    private DashboardStatsVO getCommunityStats(Long communityId) {
        // 获取该小区下的楼栋ID列表
        var buildingIds = buildingService.listAll(communityId).stream()
                .map(com.example.spms.model.vo.BuildingVO::getId)
                .toList();
        var houseList = houseService.listAll(communityId);
        var houseIds = houseList.stream()
                .map(com.example.spms.model.vo.HouseVO::getId)
                .toList();

        long totalBuildings = buildingIds.size();
        long totalHouses = houseIds.size();

        // 按房屋ID过滤的统计方法
        if (houseIds.isEmpty()) {
            return DashboardStatsVO.builder()
                    .totalCommunities(1L)
                    .totalBuildings(totalBuildings)
                    .totalHouses(0L)
                    .occupiedHouses(0L)
                    .totalOwners(0L)
                    .totalRepairs(0L)
                    .pendingRepairs(0L)
                    .processingRepairs(0L)
                    .completedRepairs(0L)
                    .totalComplaints(0L)
                    .pendingComplaints(0L)
                    .totalBills(0L)
                    .unpaidBills(0L)
                    .paidBills(0L)
                    .overdueBills(0L)
                    .totalPaymentAmount(BigDecimal.ZERO)
                    .totalOverdueAmount(BigDecimal.ZERO)
                    .billStatusDistribution(Collections.emptyMap())
                    .repairTypeDistribution(null)
                    .complaintTypeDistribution(Collections.emptyMap())
                    .build();
        }

        return DashboardStatsVO.builder()
                .totalCommunities(1L)
                .totalBuildings(totalBuildings)
                .totalHouses(totalHouses)
                .occupiedHouses(houseList.stream()
                        .filter(h -> h.getStatus() != null && h.getStatus() == 4)
                        .count())
                .totalOwners(countOwnersByHouseIds(houseIds))
                .totalRepairs(countByHouseIds(repairOrderService, houseIds))
                .pendingRepairs(countRepairsByHouseIdsAndStatus(houseIds, 0))
                .processingRepairs(countRepairsByHouseIdsAndStatus(houseIds, 1) + countRepairsByHouseIdsAndStatus(houseIds, 2))
                .completedRepairs(countRepairsByHouseIdsAndStatus(houseIds, 3))
                .totalComplaints(countByHouseIds(complaintSuggestionService, houseIds))
                .pendingComplaints(countComplaintsByHouseIdsAndStatus(houseIds, 0))
                .totalBills(countByHouseIds(billInfoService, houseIds))
                .unpaidBills(countBillsByHouseIdsAndStatus(houseIds, 0))
                .paidBills(countBillsByHouseIdsAndStatus(houseIds, 2))
                .overdueBills(countBillsByHouseIdsAndStatus(houseIds, 3))
                .totalPaymentAmount(safeDecimal(sumPaymentAmountByHouseIds(houseIds)))
                .totalOverdueAmount(safeDecimal(sumOverdueAmountByHouseIds(houseIds)))
                .billStatusDistribution(getBillStatusDistributionByHouseIds(houseIds))
                .repairTypeDistribution(null)
                .complaintTypeDistribution(getComplaintTypeDistributionByHouseIds(houseIds))
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

    /** 统计已入住房屋（status=4） */
    private long countOccupiedHouses() {
        return houseService.count(new LambdaQueryWrapper<HouseInfo>()
                .eq(HouseInfo::getStatus, 4)
                .eq(HouseInfo::getIsDeleted, 0));
    }

    private long countOwners() {
        try {
            return ownerService.listAll(null).size();
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

    // ====== 按房屋ID过滤的辅助方法 ======

    @SuppressWarnings({"rawtypes", "unchecked"})
    private long countByHouseIds(com.baomidou.mybatisplus.extension.service.IService<?> service, List<Long> houseIds) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            wrapper.in("house_id", houseIds);
            wrapper.eq("is_deleted", 0);
            return service.count(wrapper);
        } catch (Exception e) {
            return 0;
        }
    }

    private long countOwnersByHouseIds(List<Long> houseIds) {
        try {
            // 通过 owner_house_rel 表查询该小区下所有不重复的业主ID数量
            List<OwnerHouseRel> rels = ownerHouseRelService.list(new LambdaQueryWrapper<OwnerHouseRel>()
                    .select(OwnerHouseRel::getOwnerInfoId)
                    .in(OwnerHouseRel::getHouseInfoId, houseIds)
                    .eq(OwnerHouseRel::getIsDeleted, 0));
            return rels.stream()
                    .map(OwnerHouseRel::getOwnerInfoId)
                    .distinct()
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private long countRepairsByHouseIdsAndStatus(List<Long> houseIds, int status) {
        return repairOrderService.count(new LambdaQueryWrapper<RepairOrder>()
                .in(RepairOrder::getHouseId, houseIds)
                .eq(RepairOrder::getStatus, status)
                .eq(RepairOrder::getIsDeleted, 0));
    }

    private long countComplaintsByHouseIdsAndStatus(List<Long> houseIds, int status) {
        return complaintSuggestionService.count(new LambdaQueryWrapper<ComplaintSuggestion>()
                .in(ComplaintSuggestion::getHouseId, houseIds)
                .eq(ComplaintSuggestion::getStatus, status)
                .eq(ComplaintSuggestion::getIsDeleted, 0));
    }

    private long countBillsByHouseIdsAndStatus(List<Long> houseIds, int status) {
        return billInfoService.count(new LambdaQueryWrapper<BillInfo>()
                .in(BillInfo::getHouseId, houseIds)
                .eq(BillInfo::getStatus, status)
                .eq(BillInfo::getIsDeleted, 0));
    }

    private BigDecimal sumPaymentAmountByHouseIds(List<Long> houseIds) {
        try {
            // payment_record 表通过 house_id 关联
            List<PaymentRecord> records = paymentRecordService.list(new LambdaQueryWrapper<PaymentRecord>()
                    .in(PaymentRecord::getHouseId, houseIds)
                    .eq(PaymentRecord::getIsDeleted, 0));
            return records.stream()
                    .map(PaymentRecord::getPayAmount)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal sumOverdueAmountByHouseIds(List<Long> houseIds) {
        try {
            // 逾期账单（status=3）按房屋ID过滤
            List<BillInfo> overdueBills = billInfoService.list(new LambdaQueryWrapper<BillInfo>()
                    .in(BillInfo::getHouseId, houseIds)
                    .eq(BillInfo::getStatus, 3)
                    .eq(BillInfo::getIsDeleted, 0));
            return overdueBills.stream()
                    .map(b -> {
                        BigDecimal billAmount = b.getBillAmount() != null ? b.getBillAmount() : BigDecimal.ZERO;
                        BigDecimal paidAmount = b.getPaidAmount() != null ? b.getPaidAmount() : BigDecimal.ZERO;
                        return billAmount.subtract(paidAmount);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Map<String, Long> getBillStatusDistributionByHouseIds(List<Long> houseIds) {
        try {
            List<BillInfo> bills = billInfoService.list(new LambdaQueryWrapper<BillInfo>()
                    .in(BillInfo::getHouseId, houseIds)
                    .eq(BillInfo::getIsDeleted, 0));
            Map<Integer, Long> statusCountMap = bills.stream()
                    .collect(Collectors.groupingBy(
                            b -> b.getStatus() != null ? b.getStatus() : -1,
                            Collectors.counting()));
            Map<String, Long> result = new java.util.LinkedHashMap<>();
            // 按状态码映射名称
            Map<Integer, String> statusNameMap = Map.of(0, "待缴费", 1, "已逾期", 2, "已缴费", 3, "已逾期");
            for (Map.Entry<Integer, Long> entry : statusCountMap.entrySet()) {
                String name = statusNameMap.getOrDefault(entry.getKey(), "状态" + entry.getKey());
                result.merge(name, entry.getValue(), Long::sum);
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private Map<String, Long> getComplaintTypeDistributionByHouseIds(List<Long> houseIds) {
        try {
            List<ComplaintSuggestion> complaints = complaintSuggestionService.list(new LambdaQueryWrapper<ComplaintSuggestion>()
                    .in(ComplaintSuggestion::getHouseId, houseIds)
                    .eq(ComplaintSuggestion::getIsDeleted, 0));
            Map<String, Long> result = new java.util.LinkedHashMap<>();
            for (ComplaintSuggestion c : complaints) {
                String type = c.getType() != null ? c.getType() : "未知";
                result.merge(type, 1L, Long::sum);
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
