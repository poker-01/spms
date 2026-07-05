package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.FeeItemMapper;
import com.example.spms.model.bo.FeeItemQueryRequest;
import com.example.spms.model.bo.FeeItemSaveRequest;
import com.example.spms.model.bo.FeeItemUpdateRequest;
import com.example.spms.model.po.FeeItem;
import com.example.spms.model.vo.FeeItemVO;
import com.example.spms.service.FeeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 费用项目服务实现
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class FeeItemServiceImpl extends ServiceImpl<FeeItemMapper, FeeItem>
        implements FeeItemService {

    private final FeeItemMapper feeItemMapper;

    @Override
    public com.example.spms.common.Page<FeeItemVO> pageFeeItems(FeeItemQueryRequest request) {
        Page<Map<String, Object>> page = new Page<>(request.getPage(), request.getSize());
        IPage<Map<String, Object>> result = feeItemMapper.selectFeeItemPage(
                page,
                request.getItemCode(),
                request.getItemName(),
                request.getItemType(),
                request.getStatus()
        );

        List<FeeItemVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return com.example.spms.common.Page.of(result, records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFeeItem(FeeItemSaveRequest request) {
        // 检查编码是否已存在
        if (feeItemMapper.countByCode(request.getItemCode()) > 0) {
            throw new CustomException(ResultCode.FAIL, "费用项目编码已存在");
        }

        FeeItem item = new FeeItem();
        item.setItemCode(request.getItemCode());
        item.setItemName(request.getItemName());
        item.setItemType(request.getItemType());
        item.setUnitPrice(request.getUnitPrice());
        item.setUnit(request.getUnit());
        item.setCalcMethod(request.getCalcMethod());
        item.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        item.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        save(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFeeItem(FeeItemUpdateRequest request) {
        FeeItem item = getById(request.getId());
        if (item == null || item.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "费用项目不存在");
        }

        // 检查编码是否已被其他项目使用
        if (request.getItemCode() != null && 
            feeItemMapper.countByCodeExcludeSelf(request.getItemCode(), request.getId()) > 0) {
            throw new CustomException(ResultCode.FAIL, "费用项目编码已存在");
        }

        if (request.getItemCode() != null) {
            item.setItemCode(request.getItemCode());
        }
        if (request.getItemName() != null) {
            item.setItemName(request.getItemName());
        }
        if (request.getItemType() != null) {
            item.setItemType(request.getItemType());
        }
        if (request.getUnitPrice() != null) {
            item.setUnitPrice(request.getUnitPrice());
        }
        if (request.getUnit() != null) {
            item.setUnit(request.getUnit());
        }
        if (request.getCalcMethod() != null) {
            item.setCalcMethod(request.getCalcMethod());
        }
        if (request.getIsDefault() != null) {
            item.setIsDefault(request.getIsDefault());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }

        updateById(item);
    }

    @Override
    public List<FeeItem> listEnabled() {
        return list(new LambdaQueryWrapper<FeeItem>()
                .eq(FeeItem::getStatus, 1)
                .eq(FeeItem::getIsDeleted, 0)
                .orderByAsc(FeeItem::getCreateTime));
    }

    /**
     * 转换为VO
     */
    private FeeItemVO convertToVO(Map<String, Object> data) {
        Integer itemType = (Integer) data.get("item_type");
        Integer status = (Integer) data.get("status");

        return FeeItemVO.builder()
                .id(getLong(data, "id"))
                .itemCode(getString(data, "item_code"))
                .itemName(getString(data, "item_name"))
                .itemType(itemType)
                .itemTypeName(getItemTypeName(itemType))
                .unitPrice(getBigDecimal(data, "unit_price"))
                .unit(getString(data, "unit"))
                .calcMethod(getInteger(data, "calc_method"))
                .calcMethodName(getCalcMethodName(getInteger(data, "calc_method")))
                .isDefault(getInteger(data, "is_default"))
                .status(status)
                .statusName(status != null && status == 1 ? "启用" : "停用")
                .createTime(formatDate(data.get("create_time")))
                .updateTime(formatDate(data.get("update_time")))
                .build();
    }

    private String getItemTypeName(Integer type) {
        if (type == null) return "未知";
        String[] names = {"物业费", "水电费", "燃气费", "停车费", "其他"};
        return type >= 0 && type < names.length ? names[type] : "未知";
    }

    private String getCalcMethodName(Integer method) {
        if (method == null) return "未知";
        String[] names = {"固定金额", "按面积", "按户", "按表计费"};
        return method >= 0 && method < names.length ? names[method] : "未知";
    }

    // ====== 辅助方法 ======
    private Long getLong(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try { return Long.valueOf(value.toString()); } catch (NumberFormatException e) { return null; }
    }

    private String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    private java.math.BigDecimal getBigDecimal(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof java.math.BigDecimal) return (java.math.BigDecimal) value;
        try { return new java.math.BigDecimal(value.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Integer getInteger(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try { return Integer.valueOf(value.toString()); } catch (NumberFormatException e) { return null; }
    }

    private String formatDate(Object date) {
        if (date == null) return null;
        return date.toString();
    }
}