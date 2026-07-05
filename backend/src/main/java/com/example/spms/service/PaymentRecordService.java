package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.PaymentRecordQueryRequest;
import com.example.spms.model.po.PaymentRecord;
import com.example.spms.model.vo.PaymentRecordVO;

/**
 * 缴费记录服务接口
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface PaymentRecordService extends IService<PaymentRecord> {

    /**
     * 分页查询缴费记录
     */
    Page<PaymentRecordVO> pagePaymentRecords(PaymentRecordQueryRequest request);

    /**
     * 统计总缴费金额
     */
    java.math.BigDecimal sumTotalPayAmount();
}