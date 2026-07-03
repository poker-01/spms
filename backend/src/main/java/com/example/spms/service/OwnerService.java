package com.example.spms.service;

import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerRepairVO;

import java.util.List;

/**
 * 业主端服务接口
 */
public interface OwnerService {

    /**
     * 获取业主首页概览数据
     */
    OwnerHomeVO getHomeData(Long userId);

    /**
     * 获取业主账单列表
     */
    List<OwnerBillVO> listBills(Long userId);

    /**
     * 获取业主报修列表
     */
    List<OwnerRepairVO> listRepairs(Long userId);

    /**
     * 提交报修申请
     */
    void applyRepair(Long userId, RepairApplyRequest request);

    /**
     * 获取业主投诉建议列表
     */
    List<OwnerComplaintVO> listComplaints(Long userId);

    /**
     * 提交投诉建议
     */
    void applyComplaint(Long userId, ComplaintApplyRequest request);
}
