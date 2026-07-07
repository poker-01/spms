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
     * @param communityId 小区ID，为null时统计全部
     */
    Object countByStatus(Long communityId);

    /**
     * 统计总欠费金额
     * @param communityId 小区ID，为null时统计全部
     */
    java.math.BigDecimal sumOverdueAmount(Long communityId);

    /**
     * 获取业主账单列表（业主端）
     */
    List<com.example.spms.model.vo.OwnerBillVO> listOwnerBills(Long ownerId);

    /**
     * 自动生成月度账单（为指定小区所有业主、所有启用的费用项目生成）
     * @param billPeriod 账单周期，格式如 "2026-07"
     * @param communityId 小区ID，为null时生成所有小区的账单
     * @return 生成的账单数量
     */
    int autoGenerateMonthlyBills(String billPeriod, Long communityId);
}