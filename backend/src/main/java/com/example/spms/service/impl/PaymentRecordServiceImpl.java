package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.enums.PayMethod;
import com.example.spms.mapper.PaymentRecordMapper;
import com.example.spms.model.bo.PaymentRecordQueryRequest;
import com.example.spms.model.po.PaymentRecord;
import com.example.spms.model.vo.PaymentRecordVO;
import com.example.spms.service.PaymentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缴费记录服务实现
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord>
        implements PaymentRecordService {

    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    public com.example.spms.common.Page<PaymentRecordVO> pagePaymentRecords(PaymentRecordQueryRequest request) {
        Page<Map<String, Object>> page = new Page<>(request.getPage(), request.getSize());
        IPage<Map<String, Object>> result = paymentRecordMapper.selectPaymentRecordPage(
                page,
                request.getPaymentNo(),
                request.getBillId(),
                request.getOwnerId(),
                request.getHouseId(),
                request.getPayMethod(),
                request.getStartTime(),
                request.getEndTime()
        );

        List<PaymentRecordVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return com.example.spms.common.Page.of(result, records);
    }

    @Override
    public BigDecimal sumTotalPayAmount() {
        BigDecimal amount = paymentRecordMapper.sumTotalPayAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private PaymentRecordVO convertToVO(Map<String, Object> data) {
        Integer payMethod = (Integer) data.get("pay_method");
        return PaymentRecordVO.builder()
                .id(getLong(data, "id"))
                .paymentNo(getString(data, "payment_no"))
                .billId(getLong(data, "bill_id"))
                .billNo(getString(data, "bill_no"))
                .ownerId(getLong(data, "owner_id"))
                .ownerName(getString(data, "owner_name"))
                .houseNumber(getString(data, "house_number"))
                .payAmount(getBigDecimal(data, "pay_amount"))
                .payMethod(payMethod)
                .payMethodName(PayMethod.getDescByCode(payMethod != null ? payMethod : 4))
                .payTime(formatDate(data.get("pay_time")))
                .operatorName(getString(data, "operator_name"))
                .receiptNo(getString(data, "receipt_no"))
                .createTime(formatDate(data.get("create_time")))
                .build();
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

    private BigDecimal getBigDecimal(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        try { return new BigDecimal(value.toString()); } catch (NumberFormatException e) { return null; }
    }

    private String formatDate(Object date) {
        if (date == null) return null;
        return date.toString();
    }
}