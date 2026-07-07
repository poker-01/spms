package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.enums.ComplaintStatus;
import com.example.spms.enums.ComplaintType;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.ComplaintSuggestionMapper;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.ComplaintQueryRequest;
import com.example.spms.model.bo.ComplaintReplyRequest;
import com.example.spms.model.po.ComplaintSuggestion;
import com.example.spms.model.vo.ComplaintDetailVO;
import com.example.spms.model.vo.ComplaintVO;
import com.example.spms.service.ComplaintSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投诉建议服务实现
 *
 * @author SPMS
 * @date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class ComplaintSuggestionServiceImpl extends ServiceImpl<ComplaintSuggestionMapper, ComplaintSuggestion>
        implements ComplaintSuggestionService {

    private final ComplaintSuggestionMapper complaintSuggestionMapper;

    @Override
    public Page<ComplaintVO> pageComplaints(ComplaintQueryRequest request) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Map<String, Object>> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPage(), request.getSize());
        IPage<Map<String, Object>> result = complaintSuggestionMapper.selectComplaintPage(
                page,
                request.getComplaintNo(),
                request.getStatus(),
                request.getType(),
                request.getOwnerId(),
                request.getCommunityId(),
                request.getStartTime(),
                request.getEndTime()
        );

        List<ComplaintVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Page.of(result, records);
    }

    @Override
    public ComplaintDetailVO getComplaintDetail(Long complaintId) {
        Map<String, Object> data = complaintSuggestionMapper.selectComplaintDetail(complaintId);
        if (data == null || data.isEmpty()) {
            throw new CustomException(ResultCode.NOT_FOUND, "投诉不存在");
        }
        return convertToDetailVO(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitComplaint(Long userId, ComplaintApplyRequest request) {
        ComplaintSuggestion entity = new ComplaintSuggestion();
        entity.setComplaintNo("CSP" + System.currentTimeMillis());
        entity.setOwnerId(userId);
        entity.setType(request.getType());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setContactPhone(request.getContactPhone());
        entity.setStatus(ComplaintStatus.PENDING.getCode());
        entity.setIsDeleted(0);
        entity.setVersion(1);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyComplaint(ComplaintReplyRequest request) {
        ComplaintSuggestion complaint = getComplaintById(request.getComplaintId());

        if (complaint.getStatus() == ComplaintStatus.CLOSED.getCode()) {
            throw new CustomException(ResultCode.FAIL, "已关闭的投诉不可回复");
        }

        complaint.setReplyContent(request.getReplyContent());
        complaint.setReplyTime(new Date());
        complaint.setStatus(ComplaintStatus.REPLIED.getCode());
        complaint.setUpdateTime(new Date());

        updateById(complaint);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeComplaint(Long complaintId) {
        ComplaintSuggestion complaint = getComplaintById(complaintId);

        if (complaint.getStatus() == ComplaintStatus.CLOSED.getCode()) {
            throw new CustomException(ResultCode.FAIL, "该投诉已关闭");
        }

        complaint.setStatus(ComplaintStatus.CLOSED.getCode());
        complaint.setUpdateTime(new Date());

        updateById(complaint);
    }

    @Override
    public Object countByStatus() {
        List<Map<String, Object>> list = complaintSuggestionMapper.countByStatus();
        return list.stream().collect(Collectors.toMap(
                m -> {
                    Integer status = (Integer) m.get("status");
                    return ComplaintStatus.getDescByCode(status);
                },
                m -> m.get("count"),
                (v1, v2) -> v1
        ));
    }

    @Override
    public Object countByType() {
        List<Map<String, Object>> list = complaintSuggestionMapper.countByType();
        return list.stream().collect(Collectors.toMap(
                m -> {
                    String type = (String) m.get("type");
                    return type != null ? type : "其他";
                },
                m -> m.get("count"),
                (v1, v2) -> v1
        ));
    }

    /**
     * 获取投诉，不存在或已删除抛异常
     */
    private ComplaintSuggestion getComplaintById(Long complaintId) {
        ComplaintSuggestion complaint = getById(complaintId);
        if (complaint == null || complaint.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "投诉不存在");
        }
        return complaint;
    }

    /**
     * 转换为VO
     */
    private ComplaintVO convertToVO(Map<String, Object> data) {
        String type = (String) data.get("type");
        Integer status = (Integer) data.get("status");

        return ComplaintVO.builder()
                .id(getLong(data, "id"))
                .complaintNo(getString(data, "complaint_no"))
                .ownerName(getString(data, "owner_name"))
                .ownerPhone(getString(data, "owner_phone"))
                .houseNumber(getString(data, "house_number"))
                .type(type)
                .typeName(type)
                .title(getString(data, "title"))
                .content(getString(data, "content"))
                .contactPhone(getString(data, "contact_phone"))
                .status(status)
                .statusName(ComplaintStatus.getDescByCode(status != null ? status : 0))
                .replyContent(getString(data, "reply_content"))
                .replyTime(formatDate(data.get("reply_time")))
                .createTime(formatDate(data.get("create_time")))
                .updateTime(formatDate(data.get("update_time")))
                .build();
    }

    /**
     * 转换为详情VO
     */
    private ComplaintDetailVO convertToDetailVO(Map<String, Object> data) {
        String type = (String) data.get("type");
        Integer status = (Integer) data.get("status");

        return ComplaintDetailVO.builder()
                .id(getLong(data, "id"))
                .complaintNo(getString(data, "complaint_no"))
                .ownerId(getLong(data, "owner_id"))
                .ownerName(getString(data, "owner_name"))
                .ownerPhone(getString(data, "owner_phone"))
                .houseId(getLong(data, "house_id"))
                .houseNumber(getString(data, "house_number"))
                .buildingName(getString(data, "building_name"))
                .communityName(getString(data, "community_name"))
                .type(type)
                .typeName(type)
                .title(getString(data, "title"))
                .content(getString(data, "content"))
                .contactPhone(getString(data, "contact_phone"))
                .status(status)
                .statusName(ComplaintStatus.getDescByCode(status != null ? status : 0))
                .replyContent(getString(data, "reply_content"))
                .replyTime(formatDate(data.get("reply_time")))
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

    private String formatDate(Object date) {
        if (date == null) return null;
        return date.toString();
    }
}