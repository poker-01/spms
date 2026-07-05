package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.po.BillInfo;
import com.example.spms.model.po.ComplaintSuggestion;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerRepairVO;
import com.example.spms.mapper.BillInfoMapper;
import com.example.spms.mapper.ComplaintSuggestionMapper;
import com.example.spms.mapper.RepairOrderMapper;
import com.example.spms.mapper.SysUserInfoMapper;
import com.example.spms.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        order.setRepairDesc(request.getContent());
        order.setRepairPhone(request.getContactPhone());
        order.setRepairType(0);
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
}
