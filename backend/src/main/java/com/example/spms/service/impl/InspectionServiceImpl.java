package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.mapper.InspectionMapper;
import com.example.spms.model.bo.InspectionQueryRequest;
import com.example.spms.model.bo.InspectionSaveRequest;
import com.example.spms.model.po.InspectionRecord;
import com.example.spms.model.vo.InspectionVO;
import com.example.spms.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 巡检记录服务实现（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class InspectionServiceImpl extends ServiceImpl<InspectionMapper, InspectionRecord>
        implements InspectionService {

    private final InspectionMapper inspectionMapper;

    @Override
    public Page<InspectionVO> pageInspections(InspectionQueryRequest request) {
        Page<Map<String, Object>> page = new Page<>(request.getPage(), request.getSize());
        IPage<Map<String, Object>> result = inspectionMapper.selectInspectionPage(
                page,
                request.getLocation(),
                request.getResult(),
                request.getInspectorId(),
                request.getStartTime(),
                request.getEndTime()
        );

        List<InspectionVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Page.of(result, records);
    }

    @Override
    public void saveInspection(Long userId, InspectionSaveRequest request) {
        InspectionRecord record = new InspectionRecord();
        record.setInspectionNo("INS" + System.currentTimeMillis());
        record.setLocation(request.getLocation());
        record.setInspectionType(request.getInspectionType());
        record.setInspectionDesc(request.getInspectionDesc());
        record.setResult(request.getResult());
        record.setResultDesc(request.getResultDesc());
        record.setAttachments(request.getAttachments());
        record.setInspectorId(userId);
        record.setInspectionTime(request.getInspectionTime() != null ? request.getInspectionTime() : new Date());
        record.setIsDeleted(0);
        record.setVersion(1);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setCreateUser(userId);
        record.setUpdateUser(userId);
        save(record);
    }

    private InspectionVO convertToVO(Map<String, Object> data) {
        Integer result = (Integer) data.get("result");
        return InspectionVO.builder()
                .id(getLong(data, "id"))
                .inspectionNo(getString(data, "inspection_no"))
                .location(getString(data, "location"))
                .inspectionType(getInteger(data, "inspection_type"))
                .inspectionTypeName(getInspectionTypeName(getInteger(data, "inspection_type")))
                .inspectionDesc(getString(data, "inspection_desc"))
                .result(result)
                .resultName(result != null && result == 1 ? "正常" : "异常")
                .resultDesc(getString(data, "result_desc"))
                .attachments(getString(data, "attachments"))
                .inspectorName(getString(data, "inspector_name"))
                .inspectionTime(formatDate(data.get("inspection_time")))
                .createTime(formatDate(data.get("create_time")))
                .build();
    }

    private String getInspectionTypeName(Integer type) {
        if (type == null) return "未知";
        String[] names = {"设备巡检", "消防巡检", "卫生巡检", "安全巡检", "其他"};
        return type >= 0 && type < names.length ? names[type] : "未知";
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