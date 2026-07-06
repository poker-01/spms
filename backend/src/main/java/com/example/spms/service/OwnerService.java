package com.example.spms.service;

import com.example.spms.common.Page;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerRepairVO;
import com.example.spms.model.vo.OwnerVO;

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

    // ==================== 业主管理（后台） ====================

    /**
     * 分页查询业主
     */
    Page<OwnerVO> pageQuery(OwnerQueryRequest request);

    /**
     * 根据房屋ID查询主要业主
     */
    OwnerVO getOwnerByHouseId(Long houseId);

    /**
     * 查询业主关联房屋列表
     */
    List<OwnerHouseRelVO> listOwnerHouses(Long ownerId);

    /**
     * 查询全部业主（下拉列表用）
     */
    List<OwnerVO> listAll();

    /**
     * 查询尚未关联用户账号的业主（用于新增用户时选择）
     */
    List<OwnerVO> listUnlinkedOwners();

    /**
     * 查询业主详情
     */
    OwnerVO getOwnerDetail(Long id);

    /**
     * 新增业主
     */
    void saveOwner(OwnerSaveRequest request);

    /**
     * 修改业主
     */
    void updateOwner(OwnerUpdateRequest request);

    /**
     * 删除业主（软删除）
     */
    void removeById(Long id);
}
