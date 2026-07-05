package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.enums.RepairPriority;
import com.example.spms.enums.RepairStatus;
import com.example.spms.enums.RepairType;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.RepairOrderMapper;
import com.example.spms.model.bo.RepairAssignRequest;
import com.example.spms.model.bo.RepairCompleteRequest;
import com.example.spms.model.bo.RepairEvaluateRequest;
import com.example.spms.model.bo.RepairQueryRequest;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.model.po.SysUserInfo;
import com.example.spms.model.vo.RepairDetailVO;
import com.example.spms.model.vo.RepairOrderVO;
import com.example.spms.service.RepairOrderService;
import com.example.spms.service.SysUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报修工单服务实现
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder>
        implements RepairOrderService {

    private final RepairOrderMapper repairOrderMapper;
    private final SysUserInfoService sysUserInfoService;

    @Override
    public com.example.spms.common.Page<RepairOrderVO> pageRepairs(RepairQueryRequest request) {
        Page<Map<String, Object>> page = new Page<>(request.getPage(), request.getSize());
        IPage<Map<String, Object>> result = repairOrderMapper.selectRepairOrderPage(
                page,
                request.getOrderNo(),
                request.getStatus(),
                request.getRepairType(),
                request.getOwnerId(),
                request.getAssigneeId(),
                request.getStartTime(),
                request.getEndTime()
        );

        List<RepairOrderVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return com.example.spms.common.Page.of(result, records);
    }

    @Override
    public RepairDetailVO getRepairDetail(Long orderId) {
        Map<String, Object> data = repairOrderMapper.selectRepairOrderDetail(orderId);
        if (data == null || data.isEmpty()) {
            throw new CustomException(ResultCode.NOT_FOUND, "工单不存在");
        }
        return convertToDetailVO(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRepair(RepairAssignRequest request) {
        RepairOrder order = getOrderById(request.getOrderId());

        // 只有待派单状态才能派单
        if (order.getStatus() != RepairStatus.WAITING.getCode()) {
            throw new CustomException(ResultCode.FAIL, "当前状态不可派单");
        }

        // 验证维修人员是否存在
        SysUserInfo assignee = sysUserInfoService.getById(request.getAssigneeId());
        if (assignee == null || assignee.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "维修人员不存在");
        }

        order.setAssigneeId(request.getAssigneeId());
        order.setStatus(RepairStatus.ASSIGNED.getCode());
        order.setAssignTime(new Date());
        order.setUpdateTime(new Date());

        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcess(Long orderId) {
        RepairOrder order = getOrderById(orderId);

        if (order.getStatus() != RepairStatus.ASSIGNED.getCode()) {
            throw new CustomException(ResultCode.FAIL, "当前状态不可开始处理");
        }

        order.setStatus(RepairStatus.PROCESSING.getCode());
        order.setUpdateTime(new Date());

        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeRepair(RepairCompleteRequest request) {
        RepairOrder order = getOrderById(request.getOrderId());

        if (order.getStatus() != RepairStatus.PROCESSING.getCode()) {
            throw new CustomException(ResultCode.FAIL, "当前状态不可完成");
        }

        order.setStatus(RepairStatus.COMPLETED.getCode());
        order.setRepairTime(new Date());
        order.setRepairCost(request.getRepairCost());
        order.setUpdateTime(new Date());

        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateRepair(RepairEvaluateRequest request) {
        RepairOrder order = getOrderById(request.getOrderId());

        if (order.getStatus() != RepairStatus.COMPLETED.getCode()) {
            throw new CustomException(ResultCode.FAIL, "只有已完成工单可以评价");
        }

        if (order.getEvaluateScore() != null) {
            throw new CustomException(ResultCode.FAIL, "该工单已评价");
        }

        order.setEvaluateScore(request.getScore());
        order.setEvaluateComment(request.getComment());
        order.setStatus(RepairStatus.CLOSED.getCode());
        order.setUpdateTime(new Date());

        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRepair(Long orderId) {
        RepairOrder order = getOrderById(orderId);

        // 只有待派单和已派单状态可以取消
        if (order.getStatus() != RepairStatus.WAITING.getCode() &&
            order.getStatus() != RepairStatus.ASSIGNED.getCode()) {
            throw new CustomException(ResultCode.FAIL, "当前状态不可取消");
        }

        order.setStatus(RepairStatus.CANCELLED.getCode());
        order.setUpdateTime(new Date());

        updateById(order);
    }

    @Override
    public Object countByStatus() {
        List<Map<String, Object>> list = repairOrderMapper.countByStatus();
        return list.stream().collect(Collectors.toMap(
                m -> {
                    Integer status = (Integer) m.get("status");
                    return RepairStatus.getDescByCode(status);
                },
                m -> m.get("count"),
                (v1, v2) -> v1
        ));
    }

    /**
     * 获取工单，不存在或已删除抛异常
     */
    private RepairOrder getOrderById(Long orderId) {
        RepairOrder order = getById(orderId);
        if (order == null || order.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "工单不存在");
        }
        return order;
    }

    /**
     * 转换为VO
     */
    private RepairOrderVO convertToVO(Map<String, Object> data) {
        String repairType = (String) data.get("repair_type");
        Integer priority = (Integer) data.get("priority");
        Integer status = (Integer) data.get("status");

        return RepairOrderVO.builder()
                .id(getLong(data, "id"))
                .orderNo(getString(data, "order_no"))
                .ownerName(getString(data, "owner_name"))
                .ownerPhone(getString(data, "owner_phone"))
                .houseNumber(getString(data, "house_number"))
                .repairType(repairType)
                .repairTypeName(repairType)
                .repairDesc(getString(data, "repair_desc"))
                .repairPhone(getString(data, "repair_phone"))
                .priority(priority)
                .priorityName(RepairPriority.getDescByCode(priority != null ? priority : 1))
                .status(status)
                .statusName(RepairStatus.getDescByCode(status != null ? status : 0))
                .assigneeName(getString(data, "assignee_name"))
                .repairCost(getBigDecimal(data, "repair_cost"))
                .evaluateScore(getInteger(data, "evaluate_score"))
                .createTime(formatDate(data.get("create_time")))
                .updateTime(formatDate(data.get("update_time")))
                .build();
    }

    /**
     * 转换为详情VO
     */
    private RepairDetailVO convertToDetailVO(Map<String, Object> data) {
        String repairType = (String) data.get("repair_type");
        Integer priority = (Integer) data.get("priority");
        Integer status = (Integer) data.get("status");

        return RepairDetailVO.builder()
                .id(getLong(data, "id"))
                .orderNo(getString(data, "order_no"))
                .ownerId(getLong(data, "owner_id"))
                .ownerName(getString(data, "owner_name"))
                .ownerPhone(getString(data, "owner_phone"))
                .houseId(getLong(data, "house_id"))
                .houseNumber(getString(data, "house_number"))
                .buildingName(getString(data, "building_name"))
                .communityName(getString(data, "community_name"))
                .repairType(repairType)
                .repairTypeName(repairType)
                .repairDesc(getString(data, "repair_desc"))
                .repairPhone(getString(data, "repair_phone"))
                .priority(priority)
                .priorityName(RepairPriority.getDescByCode(priority != null ? priority : 1))
                .status(status)
                .statusName(RepairStatus.getDescByCode(status != null ? status : 0))
                .assigneeId(getLong(data, "assignee_id"))
                .assigneeName(getString(data, "assignee_name"))
                .assigneePhone(getString(data, "assignee_phone"))
                .assignTime(formatDate(data.get("assign_time")))
                .repairTime(formatDate(data.get("repair_time")))
                .repairCost(getBigDecimal(data, "repair_cost"))
                .repairResult(getString(data, "repair_result"))
                .evaluateScore(getInteger(data, "evaluate_score"))
                .evaluateComment(getString(data, "evaluate_comment"))
                .createTime(formatDate(data.get("create_time")))
                .updateTime(formatDate(data.get("update_time")))
                .build();
    }

    // ====== 辅助方法 ======
    private Long getLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    private BigDecimal getBigDecimal(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getInteger(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDate(Object date) {
        if (date == null) return null;
        return date.toString();
    }
}