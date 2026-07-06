package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.BillGenerateRequest;
import com.example.spms.model.bo.BillPayRequest;
import com.example.spms.model.bo.BillQueryRequest;
import com.example.spms.model.po.BillInfo;
import com.example.spms.model.vo.BillDetailVO;
import com.example.spms.model.vo.BillVO;

import java.util.List;

/**
 * 账单服务接口
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface BillInfoService extends IService<BillInfo> {

    /**
     * 分页查询账单
     */
    Page<BillVO> pageBills(BillQueryRequest request);

    /**
     * 获取账单详情
     */
    BillDetailVO getBillDetail(Long billId);

    /**
     * 生成账单
     */
    void generateBills(BillGenerateRequest request);

    /**
     * 缴费登记
     */
    void payBill(Long operatorId, BillPayRequest request);

    /**
     * 删除账单（仅待缴费状态可删除）
     */
    void deleteBill(Long billId);

    /**
     * 统计各状态账单数量
     */
    Object countByStatus();

    /**
     * 统计总欠费金额
     */
    java.math.BigDecimal sumOverdueAmount();

    /**
     * 获取业主账单列表（业主端）
     */
    List<com.example.spms.model.vo.OwnerBillVO> listOwnerBills(Long ownerId);
}