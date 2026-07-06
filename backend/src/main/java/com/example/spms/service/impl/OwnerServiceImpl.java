package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.BillInfoMapper;
import com.example.spms.mapper.BuildingInfoMapper;
import com.example.spms.mapper.CommunityInfoMapper;
import com.example.spms.mapper.ComplaintSuggestionMapper;
import com.example.spms.mapper.HouseInfoMapper;
import com.example.spms.mapper.OwnerHouseRelMapper;
import com.example.spms.mapper.OwnerInfoMapper;
import com.example.spms.mapper.RepairOrderMapper;
import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.model.bo.BillPayRequest;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.po.BillInfo;
import com.example.spms.model.po.BuildingInfo;
import com.example.spms.model.po.CommunityInfo;
import com.example.spms.model.po.ComplaintSuggestion;
import com.example.spms.model.po.HouseInfo;
import com.example.spms.model.po.OwnerHouseRel;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.vo.BillDetailVO;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerRepairVO;
import com.example.spms.model.vo.OwnerVO;
import com.example.spms.service.BillInfoService;
import com.example.spms.service.FeeItemService;
import com.example.spms.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl extends ServiceImpl<OwnerInfoMapper, OwnerInfo>
        implements OwnerService {

    private final HouseInfoMapper houseInfoMapper;
    private final BuildingInfoMapper buildingInfoMapper;
    private final CommunityInfoMapper communityInfoMapper;
    private final OwnerHouseRelMapper ownerHouseRelMapper;
    private final BillInfoMapper billInfoMapper;
    private final BillInfoService billInfoService;
    private final RepairOrderMapper repairOrderMapper;
    private final ComplaintSuggestionMapper complaintSuggestionMapper;
    private final SysUserInfoMapper sysUserInfoMapper;
    private final OwnerInfoMapper ownerInfoMapper;
    private final FeeItemService feeItemService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final java.util.Map<Integer, String> REPAIR_TYPE_MAP = java.util.Map.of(
            1, "水电维修",
            2, "家具维修",
            3, "家电维修",
            4, "其他"
    );

    private static final java.util.Map<Integer, String> REPAIR_STATUS_MAP = java.util.Map.of(
            0, "待派单",
            1, "已派单",
            2, "处理中",
            3, "已完成",
            4, "已取消",
            5, "已关闭"
    );

    private static final java.util.Map<Integer, String> COMPLAINT_TYPE_MAP = java.util.Map.of(
            1, "物业服务",
            2, "设施维修",
            3, "噪音扰民",
            4, "其他"
    );

    private static final java.util.Map<Integer, String> COMPLAINT_STATUS_MAP = java.util.Map.of(
            0, "待处理",
            1, "处理中",
            2, "已回复",
            3, "已关闭",
            4, "已取消"
    );

    @Override
    public Page<OwnerVO> pageQuery(OwnerQueryRequest request) {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getIsDeleted, 0);
        wrapper.like(StringUtils.isNotBlank(request.getOwnerName()),
                OwnerInfo::getOwnerName, request.getOwnerName());
        wrapper.eq(StringUtils.isNotBlank(request.getOwnerPhone()),
                OwnerInfo::getOwnerPhone, request.getOwnerPhone());
        wrapper.eq(request.getStatus() != null,
                OwnerInfo::getStatus, request.getStatus());
        wrapper.orderByDesc(OwnerInfo::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OwnerInfo> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNum(), request.getPageSize());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OwnerInfo> result = this.page(mpPage, wrapper);

        List<OwnerVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        Page<OwnerVO> customPage = new Page<>();
        customPage.setTotal(result.getTotal());
        customPage.setPages(result.getPages());
        customPage.setCurrent(result.getCurrent());
        customPage.setSize(result.getSize());
        customPage.setRecords(records);
        return customPage;
    }

    @Override
    @Transactional
    public void saveOwner(OwnerSaveRequest request) {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getOwnerPhone, request.getOwnerPhone())
                .eq(OwnerInfo::getIsDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new CustomException(ResultCode.FAIL, "业主电话已存在");
        }

        OwnerInfo entity = new OwnerInfo();
        BeanUtils.copyProperties(request, entity);
        entity.setIsDeleted(0);
        entity.setVersion(0);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        this.save(entity);

        if (request.getHouseId() != null) {
            OwnerHouseRel rel = new OwnerHouseRel();
            rel.setOwnerInfoId(entity.getId());
            rel.setHouseInfoId(request.getHouseId());
            rel.setRelationType(1);
            rel.setIsPrimary(1);
            rel.setIsDeleted(0);
            rel.setVersion(0);
            rel.setCreateTime(new Date());
            rel.setUpdateTime(new Date());
            ownerHouseRelMapper.insert(rel);
        }
    }

    @Override
    @Transactional
    public void updateOwner(OwnerUpdateRequest request) {
        OwnerInfo exist = this.getById(request.getId());
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }

        if (StringUtils.isNotBlank(request.getOwnerPhone())) {
            LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OwnerInfo::getOwnerPhone, request.getOwnerPhone())
                    .eq(OwnerInfo::getIsDeleted, 0)
                    .ne(OwnerInfo::getId, request.getId());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCode.FAIL, "业主电话已存在");
            }
        }

        BeanUtils.copyProperties(request, exist, "id");
        exist.setUpdateTime(new Date());
        this.updateById(exist);
    }

    @Override
    public OwnerVO getOwnerDetail(Long id) {
        OwnerInfo entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    public OwnerVO getOwnerByHouseId(Long houseId) {
        LambdaQueryWrapper<OwnerHouseRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(OwnerHouseRel::getHouseInfoId, houseId)
                .eq(OwnerHouseRel::getIsDeleted, 0)
                .orderByDesc(OwnerHouseRel::getIsPrimary)
                .last("LIMIT 1");
        OwnerHouseRel rel = ownerHouseRelMapper.selectOne(relWrapper);

        if (rel == null) {
            return null;
        }

        OwnerInfo owner = this.getById(rel.getOwnerInfoId());
        return owner != null ? toVO(owner) : null;
    }

    @Override
    public List<OwnerHouseRelVO> listOwnerHouses(Long ownerId) {
        LambdaQueryWrapper<OwnerHouseRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(OwnerHouseRel::getOwnerInfoId, ownerId)
                .eq(OwnerHouseRel::getIsDeleted, 0)
                .orderByDesc(OwnerHouseRel::getIsPrimary);
        List<OwnerHouseRel> rels = ownerHouseRelMapper.selectList(relWrapper);

        if (rels.isEmpty()) {
            return List.of();
        }

        return rels.stream().map(rel -> {
            OwnerHouseRelVO vo = new OwnerHouseRelVO();
            BeanUtils.copyProperties(rel, vo);

            HouseInfo house = houseInfoMapper.selectById(rel.getHouseInfoId());
            if (house != null) {
                BuildingInfo building = buildingInfoMapper.selectById(house.getBuildingId());
                if (building != null) {
                    CommunityInfo community = communityInfoMapper.selectById(building.getCommunityId());
                    if (community != null) {
                        vo.setHouseFullName(community.getCommunityName() + "-"
                                + building.getBuildingName() + "-"
                                + house.getHouseNumber());
                    }
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OwnerVO> listAll() {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getIsDeleted, 0)
                .orderByAsc(OwnerInfo::getOwnerName);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerVO> listUnlinkedOwners() {
        return ownerInfoMapper.selectUnlinkedOwners().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeById(Long id) {
        OwnerInfo exist = this.getById(id);
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        exist.setIsDeleted(1);
        exist.setUpdateTime(new Date());
        this.updateById(exist);
    }

    // ========== 业主端接口实现 ==========

    @Override
    public OwnerHomeVO getHomeData(Long userId) {
        SysUserInfo user = sysUserInfoMapper.selectById(userId);
        if (user == null) {
            throw new CustomException(ResultCode.USER_NOT_FOUND);
        }

        List<String> roles = sysUserInfoMapper.selectRoleCodesByUserId(userId);

        LambdaQueryWrapper<OwnerInfo> ownerWrapper = new LambdaQueryWrapper<>();
        ownerWrapper.eq(OwnerInfo::getOwnerPhone, user.getPhoneNumber())
                .eq(OwnerInfo::getIsDeleted, 0);
        OwnerInfo owner = this.getOne(ownerWrapper);

        long pendingBillCount = 0;
        long repairCount = 0;
        long complaintCount = 0;

        if (owner != null) {
            pendingBillCount = billInfoMapper.selectCount(
                    new LambdaQueryWrapper<BillInfo>()
                            .eq(BillInfo::getOwnerId, owner.getId())
                            .eq(BillInfo::getStatus, 0)
                            .eq(BillInfo::getIsDeleted, 0)
            );

            repairCount = repairOrderMapper.selectCount(
                    new LambdaQueryWrapper<RepairOrder>()
                            .eq(RepairOrder::getOwnerId, owner.getId())
                            .eq(RepairOrder::getIsDeleted, 0)
            );

            complaintCount = complaintSuggestionMapper.selectCount(
                    new LambdaQueryWrapper<ComplaintSuggestion>()
                            .eq(ComplaintSuggestion::getOwnerId, owner.getId())
                            .eq(ComplaintSuggestion::getIsDeleted, 0)
            );
        }

        return OwnerHomeVO.builder()
                .userId(userId)
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .roles(roles)
                .pendingBillCount((int) pendingBillCount)
                .repairCount((int) repairCount)
                .complaintCount((int) complaintCount)
                .build();
    }

    @Override
    public List<OwnerBillVO> listBills(Long userId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            return List.of();
        }

        List<BillInfo> bills = billInfoMapper.selectList(
                new LambdaQueryWrapper<BillInfo>()
                        .eq(BillInfo::getOwnerId, owner.getId())
                        .eq(BillInfo::getIsDeleted, 0)
                        .orderByDesc(BillInfo::getCreateTime)
        );

        return bills.stream().map(bill -> {
            OwnerBillVO vo = OwnerBillVO.builder()
                    .id(bill.getId())
                    .billNo(bill.getBillNo())
                    .itemName(getItemName(bill.getFeeItemId()))
                    .amount(bill.getBillAmount())
                    .status(bill.getStatus())
                    .createTime(formatDate(bill.getCreateTime()))
                    .deadline(formatDate(bill.getPayDeadline()))
                    .build();
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public BillDetailVO getBillDetail(Long userId, Long billId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        BillInfo bill = billInfoMapper.selectOne(
                new LambdaQueryWrapper<BillInfo>()
                        .eq(BillInfo::getId, billId)
                        .eq(BillInfo::getOwnerId, owner.getId())
                        .eq(BillInfo::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
        if (bill == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "账单不存在或无权查看");
        }

        return billInfoService.getBillDetail(billId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payBill(Long userId, Long billId, Integer payMethod) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        BillInfo bill = billInfoMapper.selectOne(
                new LambdaQueryWrapper<BillInfo>()
                        .eq(BillInfo::getId, billId)
                        .eq(BillInfo::getOwnerId, owner.getId())
                        .eq(BillInfo::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
        if (bill == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "账单不存在或无权查看");
        }

        BillPayRequest request = new BillPayRequest();
        request.setBillId(billId);
        request.setPayAmount(bill.getBillAmount().subtract(bill.getPaidAmount()));
        request.setPayMethod(payMethod);
        billInfoService.payBill(userId, request);
    }

    private String getItemName(Long feeItemId) {
        if (feeItemId == null) {
            return "未知项目";
        }
        com.example.spms.model.po.FeeItem item = feeItemService.getById(feeItemId);
        return item != null ? item.getItemName() : "未知项目";
    }

    @Override
    public List<OwnerRepairVO> listRepairs(Long userId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            return List.of();
        }

        List<RepairOrder> repairs = repairOrderMapper.selectList(
                new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getOwnerId, owner.getId())
                        .eq(RepairOrder::getIsDeleted, 0)
                        .orderByDesc(RepairOrder::getCreateTime)
        );

        return repairs.stream().map(repair -> {
            OwnerRepairVO vo = OwnerRepairVO.builder()
                    .id(repair.getId())
                    .orderNo(repair.getOrderNo())
                    .repairType(repair.getRepairType())
                    .repairTypeName(repair.getRepairType())
                    .repairDesc(repair.getRepairDesc())
                    .repairPhone(repair.getRepairPhone())
                    .priority(repair.getPriority())
                    .status(repair.getStatus())
                    .statusName(REPAIR_STATUS_MAP.getOrDefault(repair.getStatus(), "未知"))
                    .repairCost(repair.getRepairCost())
                    .evaluateScore(repair.getEvaluateScore())
                    .evaluateComment(repair.getEvaluateComment())
                    .createTime(formatDate(repair.getCreateTime()))
                    .repairTime(formatDate(repair.getRepairTime()))
                    .build();
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public OwnerRepairVO getRepairDetail(Long userId, Long orderId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        RepairOrder repair = repairOrderMapper.selectOne(
                new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getId, orderId)
                        .eq(RepairOrder::getOwnerId, owner.getId())
                        .eq(RepairOrder::getIsDeleted, 0)
                        .last("LIMIT 1")
        );

        if (repair == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "报修单不存在或无权查看");
        }

        return OwnerRepairVO.builder()
                .id(repair.getId())
                .orderNo(repair.getOrderNo())
                .repairType(repair.getRepairType())
                .repairTypeName(repair.getRepairType())
                .repairDesc(repair.getRepairDesc())
                .repairPhone(repair.getRepairPhone())
                .priority(repair.getPriority())
                .status(repair.getStatus())
                .statusName(REPAIR_STATUS_MAP.getOrDefault(repair.getStatus(), "未知"))
                .repairCost(repair.getRepairCost())
                .evaluateScore(repair.getEvaluateScore())
                .evaluateComment(repair.getEvaluateComment())
                .createTime(formatDate(repair.getCreateTime()))
                .repairTime(formatDate(repair.getRepairTime()))
                .build();
    }

    @Override
    @Transactional
    public void applyRepair(Long userId, RepairApplyRequest request) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        OwnerHouseRel rel = ownerHouseRelMapper.selectOne(
                new LambdaQueryWrapper<OwnerHouseRel>()
                        .eq(OwnerHouseRel::getOwnerInfoId, owner.getId())
                        .eq(OwnerHouseRel::getIsPrimary, 1)
                        .eq(OwnerHouseRel::getIsDeleted, 0)
                        .last("LIMIT 1")
        );

        if (rel == null) {
            throw new CustomException(ResultCode.FAIL, "该业主未关联房屋");
        }

        String orderNo = "REP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        RepairOrder repair = new RepairOrder();
        repair.setOrderNo(orderNo);
        repair.setHouseId(rel.getHouseInfoId());
        repair.setOwnerId(owner.getId());
        repair.setRepairType(REPAIR_TYPE_MAP.getOrDefault(request.getRepairType(), "其他"));
        repair.setRepairDesc(request.getContent());
        repair.setRepairPhone(StringUtils.isNotBlank(request.getContactPhone())
                ? request.getContactPhone() : owner.getOwnerPhone());
        repair.setStatus(0);
        repair.setPriority(2);
        repair.setIsDeleted(0);
        repair.setVersion(0);

        repairOrderMapper.insert(repair);
    }

    @Override
    public List<OwnerComplaintVO> listComplaints(Long userId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            return List.of();
        }

        List<ComplaintSuggestion> complaints = complaintSuggestionMapper.selectList(
                new LambdaQueryWrapper<ComplaintSuggestion>()
                        .eq(ComplaintSuggestion::getOwnerId, owner.getId())
                        .eq(ComplaintSuggestion::getIsDeleted, 0)
                        .orderByDesc(ComplaintSuggestion::getCreateTime)
        );

        return complaints.stream().map(complaint -> {
            int typeCode = parseIntSafe(complaint.getType());
            OwnerComplaintVO vo = OwnerComplaintVO.builder()
                    .id(complaint.getId())
                    .complaintNo(complaint.getComplaintNo())
                    .type(typeCode)
                    .typeName(COMPLAINT_TYPE_MAP.getOrDefault(typeCode, "其他"))
                    .title(complaint.getTitle())
                    .content(complaint.getContent())
                    .contactPhone(complaint.getContactPhone())
                    .status(complaint.getStatus())
                    .statusName(COMPLAINT_STATUS_MAP.getOrDefault(complaint.getStatus(), "未知"))
                    .replyContent(complaint.getReplyContent())
                    .replyTime(formatDate(complaint.getReplyTime()))
                    .createTime(formatDate(complaint.getCreateTime()))
                    .build();
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyComplaint(Long userId, ComplaintApplyRequest request) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        OwnerHouseRel rel = ownerHouseRelMapper.selectOne(
                new LambdaQueryWrapper<OwnerHouseRel>()
                        .eq(OwnerHouseRel::getOwnerInfoId, owner.getId())
                        .eq(OwnerHouseRel::getIsPrimary, 1)
                        .eq(OwnerHouseRel::getIsDeleted, 0)
                        .last("LIMIT 1")
        );

        String complaintNo = "CPL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ComplaintSuggestion complaint = new ComplaintSuggestion();
        complaint.setComplaintNo(complaintNo);
        if (rel != null) {
            complaint.setHouseId(rel.getHouseInfoId());
        }
        complaint.setOwnerId(owner.getId());
        complaint.setType(request.getType());
        complaint.setTitle(request.getTitle());
        complaint.setContent(request.getContent());
        complaint.setContactPhone(StringUtils.isNotBlank(request.getContactPhone())
                ? request.getContactPhone() : owner.getOwnerPhone());
        complaint.setStatus(0);
        complaint.setIsDeleted(0);
        complaint.setVersion(0);

        complaintSuggestionMapper.insert(complaint);
    }

    @Override
    public OwnerComplaintVO getComplaintDetail(Long userId, Long complaintId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        ComplaintSuggestion complaint = complaintSuggestionMapper.selectOne(
                new LambdaQueryWrapper<ComplaintSuggestion>()
                        .eq(ComplaintSuggestion::getId, complaintId)
                        .eq(ComplaintSuggestion::getOwnerId, owner.getId())
                        .eq(ComplaintSuggestion::getIsDeleted, 0)
                        .last("LIMIT 1")
        );

        if (complaint == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "投诉记录不存在或无权查看");
        }

        int typeCode = parseIntSafe(complaint.getType());
        return OwnerComplaintVO.builder()
                .id(complaint.getId())
                .complaintNo(complaint.getComplaintNo())
                .type(typeCode)
                .typeName(COMPLAINT_TYPE_MAP.getOrDefault(typeCode, "其他"))
                .title(complaint.getTitle())
                .content(complaint.getContent())
                .contactPhone(complaint.getContactPhone())
                .status(complaint.getStatus())
                .statusName(COMPLAINT_STATUS_MAP.getOrDefault(complaint.getStatus(), "未知"))
                .replyContent(complaint.getReplyContent())
                .replyTime(formatDate(complaint.getReplyTime()))
                .createTime(formatDate(complaint.getCreateTime()))
                .build();
    }

    @Override
    @Transactional
    public void cancelComplaint(Long userId, Long complaintId) {
        OwnerInfo owner = getOwnerByUserId(userId);
        if (owner == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "业主信息不存在");
        }

        ComplaintSuggestion complaint = complaintSuggestionMapper.selectOne(
                new LambdaQueryWrapper<ComplaintSuggestion>()
                        .eq(ComplaintSuggestion::getId, complaintId)
                        .eq(ComplaintSuggestion::getOwnerId, owner.getId())
                        .eq(ComplaintSuggestion::getIsDeleted, 0)
                        .last("LIMIT 1")
        );

        if (complaint == null) {
            throw new CustomException(ResultCode.NOT_FOUND, "投诉记录不存在或无权操作");
        }

        if (complaint.getStatus() != 0) {
            throw new CustomException(ResultCode.FAIL, "只有待处理的投诉才能取消");
        }

        complaint.setStatus(4);
        complaintSuggestionMapper.updateById(complaint);
    }

    // ========== 私有辅助方法 ==========

    private int parseIntSafe(String value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private OwnerVO toVO(OwnerInfo entity) {
        OwnerVO vo = new OwnerVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private OwnerInfo getOwnerByUserId(Long userId) {
        SysUserInfo user = sysUserInfoMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getOwnerPhone, user.getPhoneNumber())
                .eq(OwnerInfo::getIsDeleted, 0);
        return this.getOne(wrapper);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }
}
