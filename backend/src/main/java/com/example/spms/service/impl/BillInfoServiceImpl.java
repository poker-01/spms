package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.enums.BillStatus;
import com.example.spms.enums.PayMethod;
import com.example.spms.enums.ResultCode;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.BillInfoMapper;
import com.example.spms.mapper.PaymentRecordMapper;
import com.example.spms.model.bo.BillGenerateRequest;
import com.example.spms.model.bo.BillPayRequest;
import com.example.spms.model.bo.BillQueryRequest;
import com.example.spms.model.po.BillInfo;
import com.example.spms.model.po.FeeItem;
import com.example.spms.model.po.OwnerHouseRel;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.po.PaymentRecord;
import com.example.spms.model.vo.BillDetailVO;
import com.example.spms.model.vo.BillVO;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.PaymentRecordVO;
import com.example.spms.service.BillInfoService;
import com.example.spms.service.FeeItemService;
import com.example.spms.service.OwnerHouseRelService;
import com.example.spms.service.OwnerInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账单服务实现
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Service
@RequiredArgsConstructor
public class BillInfoServiceImpl extends ServiceImpl<BillInfoMapper, BillInfo>
        implements BillInfoService {

    private final BillInfoMapper billInfoMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final FeeItemService feeItemService;
    private final OwnerInfoService ownerInfoService;
    private final OwnerHouseRelService ownerHouseRelService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public com.example.spms.common.Page<BillVO> pageBills(BillQueryRequest request) {
        Page<Map<String, Object>> page = new Page<>(request.getPage(), request.getSize());
        IPage<Map<String, Object>> result = billInfoMapper.selectBillPage(
                page,
                request.getBillNo(),
                request.getOwnerId(),
                request.getHouseId(),
                request.getFeeItemId(),
                request.getStatus(),
                request.getBillPeriod(),
                request.getStartTime(),
                request.getEndTime()
        );

        List<BillVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return com.example.spms.common.Page.of(result, records);
    }

    @Override
    public BillDetailVO getBillDetail(Long billId) {
        Map<String, Object> data = billInfoMapper.selectBillDetail(billId);
        if (data == null || data.isEmpty()) {
            throw new CustomException(ResultCode.NOT_FOUND, "账单不存在");
        }

        BillDetailVO detail = convertToDetailVO(data);

        // 查询缴费记录
        List<Map<String, Object>> paymentRecords = paymentRecordMapper.selectByBillId(billId);
        List<PaymentRecordVO> paymentVOs = paymentRecords.stream()
                .map(this::convertToPaymentVO)
                .collect(Collectors.toList());
        detail.setPaymentRecords(paymentVOs);

        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateBills(BillGenerateRequest request) {
        // 验证费用项目
        FeeItem feeItem = feeItemService.getById(request.getFeeItemId());
        if (feeItem == null || feeItem.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "费用项目不存在");
        }

        // 验证业主
        List<OwnerInfo> owners = ownerInfoService.listByIds(request.getOwnerIds());
        if (owners.size() != request.getOwnerIds().size()) {
            throw new CustomException(ResultCode.NOT_FOUND, "部分业主不存在");
        }

        // 查询业主关联的房屋（取主房屋）
        List<OwnerHouseRel> ownerHouseRels = ownerHouseRelService.list(
                new LambdaQueryWrapper<OwnerHouseRel>()
                        .in(OwnerHouseRel::getOwnerInfoId, request.getOwnerIds())
                        .eq(OwnerHouseRel::getIsPrimary, 1)
                        .eq(OwnerHouseRel::getIsDeleted, 0));
        Map<Long, Long> ownerHouseMap = ownerHouseRels.stream()
                .collect(Collectors.toMap(OwnerHouseRel::getOwnerInfoId, OwnerHouseRel::getHouseInfoId, (v1, v2) -> v1));

        List<BillInfo> bills = new ArrayList<>();
        Date now = new Date();
        Date deadline = request.getPayDeadline() != null 
                ? java.sql.Timestamp.valueOf(request.getPayDeadline().atStartOfDay())
                : null;

        for (OwnerInfo owner : owners) {
            Long houseId = ownerHouseMap.get(owner.getId());
            if (houseId == null) {
                throw new CustomException(ResultCode.FAIL, "业主 " + owner.getOwnerName() + " 未关联主房屋，无法生成账单");
            }
            BillInfo bill = new BillInfo();
            bill.setBillNo("BILL" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000)));
            bill.setHouseId(houseId);
            bill.setOwnerId(owner.getId());
            bill.setFeeItemId(request.getFeeItemId());
            bill.setBillPeriod(request.getBillPeriod());
            bill.setBillAmount(request.getBillAmount());
            bill.setPaidAmount(BigDecimal.ZERO);
            bill.setStatus(BillStatus.UNPAID.getCode());
            bill.setPayDeadline(deadline);
            bill.setIsDeleted(0);
            bill.setVersion(1);
            bill.setCreateTime(now);
            bill.setUpdateTime(now);
            bills.add(bill);
        }

        saveBatch(bills);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payBill(Long operatorId, BillPayRequest request) {
        BillInfo bill = getById(request.getBillId());
        if (bill == null || bill.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "账单不存在");
        }

        // 检查是否已完全缴费
        if (bill.getStatus() == BillStatus.PAID.getCode()) {
            throw new CustomException(ResultCode.FAIL, "该账单已完全缴费");
        }

        // 检查缴费金额是否超过剩余应缴金额
        BigDecimal remaining = bill.getBillAmount().subtract(bill.getPaidAmount());
        if (request.getPayAmount().compareTo(remaining) > 0) {
            throw new CustomException(ResultCode.FAIL, "缴费金额不能超过剩余应缴金额");
        }

        // 更新账单已付金额
        int updated = billInfoMapper.updatePaidAmount(request.getBillId(), request.getPayAmount());
        if (updated == 0) {
            throw new CustomException(ResultCode.FAIL, "缴费失败，请重试");
        }

        // 查询更新后的账单状态
        BillInfo updatedBill = getById(request.getBillId());

        // 创建缴费记录
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo("PAY" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000)));
        record.setBillId(request.getBillId());
        record.setHouseId(bill.getHouseId());
        record.setOwnerId(bill.getOwnerId());
        record.setPayAmount(request.getPayAmount());
        record.setPayMethod(request.getPayMethod());
        record.setPayTime(new Date());
        record.setOperatorId(operatorId);
        record.setReceiptNo(request.getReceiptNo());
        record.setIsDeleted(0);
        record.setVersion(1);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setCreateUser(operatorId);
        record.setUpdateUser(operatorId);

        paymentRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBill(Long billId) {
        BillInfo bill = getById(billId);
        if (bill == null || bill.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "账单不存在");
        }
        if (bill.getStatus() != BillStatus.UNPAID.getCode()) {
            throw new CustomException(ResultCode.FAIL, "仅待缴费状态的账单可删除");
        }
        bill.setIsDeleted(1);
        updateById(bill);
    }

    @Override
    public Object countByStatus() {
        List<Map<String, Object>> list = billInfoMapper.countByStatus();
        return list.stream().collect(Collectors.toMap(
                m -> {
                    Integer status = (Integer) m.get("status");
                    return BillStatus.getDescByCode(status);
                },
                m -> m.get("count"),
                (v1, v2) -> v1
        ));
    }

    @Override
    public BigDecimal sumOverdueAmount() {
        BigDecimal amount = billInfoMapper.sumOverdueAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    public List<OwnerBillVO> listOwnerBills(Long ownerId) {
        List<BillInfo> bills = list(new LambdaQueryWrapper<BillInfo>()
                .eq(BillInfo::getOwnerId, ownerId)
                .eq(BillInfo::getIsDeleted, 0)
                .orderByDesc(BillInfo::getCreateTime));

        return bills.stream().map(b -> OwnerBillVO.builder()
                .id(b.getId())
                .billNo(b.getBillNo())
                .itemName(getItemName(b.getFeeItemId()))
                .amount(b.getBillAmount())
                .status(b.getStatus())
                .createTime(formatDate(b.getCreateTime()))
                .deadline(formatDate(b.getPayDeadline()))
                .build()
        ).collect(Collectors.toList());
    }

    private String getItemName(Long feeItemId) {
        if (feeItemId == null) return "未知项目";
        FeeItem item = feeItemService.getById(feeItemId);
        return item != null ? item.getItemName() : "未知项目";
    }

    // ====== 转换方法 ======

    private BillVO convertToVO(Map<String, Object> data) {
        Integer status = (Integer) data.get("status");
        return BillVO.builder()
                .id(getLong(data, "id"))
                .billNo(getString(data, "bill_no"))
                .ownerName(getString(data, "owner_name"))
                .ownerPhone(getString(data, "owner_phone"))
                .houseNumber(getString(data, "house_number"))
                .buildingName(getString(data, "building_name"))
                .itemName(getString(data, "item_name"))
                .billPeriod(getString(data, "bill_period"))
                .billAmount(getBigDecimal(data, "bill_amount"))
                .paidAmount(getBigDecimal(data, "paid_amount"))
                .status(status)
                .statusName(BillStatus.getDescByCode(status != null ? status : 0))
                .payDeadline(formatDate(data.get("pay_deadline")))
                .payTime(formatDate(data.get("pay_time")))
                .createTime(formatDate(data.get("create_time")))
                .updateTime(formatDate(data.get("update_time")))
                .build();
    }

    private BillDetailVO convertToDetailVO(Map<String, Object> data) {
        Integer status = (Integer) data.get("status");
        Integer payMethod = (Integer) data.get("pay_method");
        return BillDetailVO.builder()
                .id(getLong(data, "id"))
                .billNo(getString(data, "bill_no"))
                .ownerId(getLong(data, "owner_id"))
                .ownerName(getString(data, "owner_name"))
                .ownerPhone(getString(data, "owner_phone"))
                .houseId(getLong(data, "house_id"))
                .houseNumber(getString(data, "house_number"))
                .buildingName(getString(data, "building_name"))
                .communityName(getString(data, "community_name"))
                .feeItemId(getLong(data, "fee_item_id"))
                .itemName(getString(data, "item_name"))
                .itemCode(getString(data, "item_code"))
                .billPeriod(getString(data, "bill_period"))
                .billAmount(getBigDecimal(data, "bill_amount"))
                .paidAmount(getBigDecimal(data, "paid_amount"))
                .status(status)
                .statusName(BillStatus.getDescByCode(status != null ? status : 0))
                .payDeadline(formatDate(data.get("pay_deadline")))
                .payTime(formatDate(data.get("pay_time")))
                .payMethod(payMethod)
                .payMethodName(PayMethod.getDescByCode(payMethod != null ? payMethod : 4))
                .createTime(formatDate(data.get("create_time")))
                .updateTime(formatDate(data.get("update_time")))
                .build();
    }

    private PaymentRecordVO convertToPaymentVO(Map<String, Object> data) {
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