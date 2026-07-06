package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.spms.common.Page;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.BillInfoMapper;
import com.example.spms.mapper.ComplaintSuggestionMapper;
import com.example.spms.mapper.OwnerHouseRelMapper;
import com.example.spms.mapper.OwnerInfoMapper;
import com.example.spms.mapper.RepairOrderMapper;
import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.po.BillInfo;
import com.example.spms.model.po.ComplaintSuggestion;
import com.example.spms.model.po.OwnerHouseRel;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerRepairVO;
import com.example.spms.model.vo.OwnerVO;
import com.example.spms.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业主端服务实现
 */
@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final BillInfoMapper billInfoMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final ComplaintSuggestionMapper complaintSuggestionMapper;
    private final SysUserInfoMapper sysUserInfoMapper;
    private final OwnerInfoMapper ownerInfoMapper;
    private final OwnerHouseRelMapper ownerHouseRelMapper;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public OwnerHomeVO getHomeData(Long userId) {
        SysUserInfo user = sysUserInfoMapper.selectById(userId);

        long pendingBills = billInfoMapper.selectCount(
                new LambdaQueryWrapper<BillInfo>()
                        .eq(BillInfo::getOwnerId, userId)
                        .eq(BillInfo::getStatus, 0)
                        .eq(BillInfo::getIsDeleted, 0)
        );

        long repairs = repairOrderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getOwnerId, userId)
                        .eq(RepairOrder::getIsDeleted, 0)
        );

        long complaints = complaintSuggestionMapper.selectCount(
                new LambdaQueryWrapper<ComplaintSuggestion>()
                        .eq(ComplaintSuggestion::getOwnerId, userId)
                        .eq(ComplaintSuggestion::getIsDeleted, 0)
        );

        return OwnerHomeVO.builder()
                .userId(userId)
                .userName(user != null ? user.getUserName() : null)
                .fullName(user != null ? user.getFullName() : null)
                .phoneNumber(user != null ? user.getPhoneNumber() : null)
                .roles(null)
                .pendingBillCount((int) pendingBills)
                .repairCount((int) repairs)
                .complaintCount((int) complaints)
                .build();
    }

    @Override
    public List<OwnerBillVO> listBills(Long userId) {
        List<BillInfo> bills = billInfoMapper.selectList(
                new LambdaQueryWrapper<BillInfo>()
                        .eq(BillInfo::getOwnerId, userId)
                        .eq(BillInfo::getIsDeleted, 0)
                        .orderByDesc(BillInfo::getCreateTime)
        );
        return bills.stream().map(b -> OwnerBillVO.builder()
                .id(b.getId())
                .billNo(b.getBillNo())
                .itemName(b.getBillPeriod())
                .amount(b.getBillAmount())
                .status(b.getStatus())
                .createTime(b.getCreateTime() != null ? SDF.format(b.getCreateTime()) : null)
                .deadline(b.getPayDeadline() != null ? SDF.format(b.getPayDeadline()) : null)
                .build()
        ).toList();
    }

    @Override
    public List<OwnerRepairVO> listRepairs(Long userId) {
        List<RepairOrder> orders = repairOrderMapper.selectList(
                new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getOwnerId, userId)
                        .eq(RepairOrder::getIsDeleted, 0)
                        .orderByDesc(RepairOrder::getCreateTime)
        );
        return orders.stream().map(o -> OwnerRepairVO.builder()
                .id(o.getId())
                .orderNo(o.getOrderNo())
                .repairType(o.getRepairType())
                .repairDesc(o.getRepairDesc())
                .repairPhone(o.getRepairPhone())
                .priority(o.getPriority())
                .status(o.getStatus())
                .repairCost(o.getRepairCost())
                .evaluateScore(o.getEvaluateScore())
                .evaluateComment(o.getEvaluateComment())
                .createTime(o.getCreateTime() != null ? SDF.format(o.getCreateTime()) : null)
                .repairTime(o.getRepairTime() != null ? SDF.format(o.getRepairTime()) : null)
                .build()
        ).toList();
    }

    @Override
    public void applyRepair(Long userId, RepairApplyRequest request) {
        RepairOrder order = new RepairOrder();
        order.setOrderNo("RPR" + System.currentTimeMillis());
        order.setOwnerId(userId);
        order.setRepairType(request.getRepairType());
        order.setRepairDesc(request.getContent());
        order.setRepairPhone(request.getContactPhone());
        order.setPriority(0);
        order.setStatus(0);
        order.setIsDeleted(0);
        order.setVersion(1);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setCreateUser(userId);
        order.setUpdateUser(userId);
        repairOrderMapper.insert(order);
    }

    @Override
    public List<OwnerComplaintVO> listComplaints(Long userId) {
        List<ComplaintSuggestion> list = complaintSuggestionMapper.selectList(
                new LambdaQueryWrapper<ComplaintSuggestion>()
                        .eq(ComplaintSuggestion::getOwnerId, userId)
                        .eq(ComplaintSuggestion::getIsDeleted, 0)
                        .orderByDesc(ComplaintSuggestion::getCreateTime)
        );
        return list.stream().map(c -> OwnerComplaintVO.builder()
                .id(c.getId())
                .complaintNo(c.getComplaintNo())
                .type(c.getType())
                .title(c.getTitle())
                .content(c.getContent())
                .contactPhone(c.getContactPhone())
                .status(c.getStatus())
                .replyContent(c.getReplyContent())
                .replyTime(c.getReplyTime() != null ? SDF.format(c.getReplyTime()) : null)
                .createTime(c.getCreateTime() != null ? SDF.format(c.getCreateTime()) : null)
                .build()
        ).toList();
    }

    @Override
    public void applyComplaint(Long userId, ComplaintApplyRequest request) {
        ComplaintSuggestion entity = new ComplaintSuggestion();
        entity.setComplaintNo("CSP" + System.currentTimeMillis());
        entity.setOwnerId(userId);
        entity.setType(request.getType());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setContactPhone(request.getContactPhone());
        entity.setStatus(0);
        entity.setIsDeleted(0);
        entity.setVersion(1);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);
        complaintSuggestionMapper.insert(entity);
    }

    // ==================== 业主管理（后台） ====================

    @Override
    public Page<OwnerVO> pageQuery(OwnerQueryRequest request) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OwnerInfo> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getIsDeleted, 0);
        wrapper.like(StringUtils.isNotBlank(request.getOwnerName()),
                OwnerInfo::getOwnerName, request.getOwnerName());
        wrapper.like(StringUtils.isNotBlank(request.getOwnerPhone()),
                OwnerInfo::getOwnerPhone, request.getOwnerPhone());
        wrapper.eq(request.getStatus() != null,
                OwnerInfo::getStatus, request.getStatus());
        wrapper.orderByDesc(OwnerInfo::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OwnerInfo> result =
                ownerInfoMapper.selectPage(mpPage, wrapper);

        List<OwnerVO> records = result.getRecords().stream()
                .map(this::toOwnerVO)
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
    public OwnerVO getOwnerByHouseId(Long houseId) {
        OwnerInfo owner = ownerInfoMapper.selectByHouseId(houseId);
        return owner == null ? null : toOwnerVO(owner);
    }

    @Override
    public List<OwnerHouseRelVO> listOwnerHouses(Long ownerId) {
        List<OwnerHouseRel> list = ownerHouseRelMapper.selectOwnerHouseDetail(ownerId, null);
        return list.stream().map(rel -> {
            OwnerHouseRelVO vo = new OwnerHouseRelVO();
            vo.setId(rel.getId());
            vo.setOwnerInfoId(rel.getOwnerInfoId());
            vo.setHouseInfoId(rel.getHouseInfoId());
            vo.setRelationType(rel.getRelationType());
            vo.setIsPrimary(rel.getIsPrimary());
            StringBuilder fullName = new StringBuilder();
            if (rel.getCommunityName() != null) fullName.append(rel.getCommunityName());
            if (rel.getBuildingName() != null) fullName.append(rel.getBuildingName());
            if (rel.getHouseNumber() != null) fullName.append(rel.getHouseNumber());
            vo.setHouseFullName(fullName.toString());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OwnerVO> listAll() {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getIsDeleted, 0)
                .orderByAsc(OwnerInfo::getOwnerName);
        return ownerInfoMapper.selectList(wrapper).stream()
                .map(this::toOwnerVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerVO> listUnlinkedOwners() {
        return ownerInfoMapper.selectUnlinkedOwners().stream()
                .map(this::toOwnerVO)
                .collect(Collectors.toList());
    }

    @Override
    public OwnerVO getOwnerDetail(Long id) {
        OwnerInfo entity = ownerInfoMapper.selectDetailById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        return toOwnerVO(entity);
    }

    @Override
    @Transactional
    public void saveOwner(OwnerSaveRequest request) {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getOwnerPhone, request.getOwnerPhone())
                .eq(OwnerInfo::getIsDeleted, 0);
        if (ownerInfoMapper.selectCount(wrapper) > 0) {
            throw new CustomException(ResultCode.FAIL, "业主电话已存在");
        }

        OwnerInfo entity = new OwnerInfo();
        BeanUtils.copyProperties(request, entity);
        entity.setIsDeleted(0);
        entity.setVersion(0);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        ownerInfoMapper.insert(entity);

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
        OwnerInfo exist = ownerInfoMapper.selectById(request.getId());
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }

        BeanUtils.copyProperties(request, exist, "id");
        exist.setUpdateTime(new Date());
        ownerInfoMapper.updateById(exist);
    }

    @Override
    @Transactional
    public void removeById(Long id) {
        OwnerInfo exist = ownerInfoMapper.selectById(id);
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        ownerInfoMapper.deleteById(id);
    }

    private OwnerVO toOwnerVO(OwnerInfo entity) {
        OwnerVO vo = new OwnerVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
