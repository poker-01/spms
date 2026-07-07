package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.bo.RepairApplyRequest;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.vo.OwnerBillVO;
import com.example.spms.model.vo.OwnerComplaintVO;
import com.example.spms.model.vo.OwnerHomeVO;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerRepairVO;
import com.example.spms.model.vo.OwnerVO;

import java.util.List;

public interface OwnerService extends IService<OwnerInfo> {

    Page<OwnerVO> pageQuery(OwnerQueryRequest request, Long communityId);

    void saveOwner(OwnerSaveRequest request);

    void updateOwner(OwnerUpdateRequest request);

    OwnerVO getOwnerDetail(Long id);

    OwnerVO getOwnerByHouseId(Long houseId);

    List<OwnerHouseRelVO> listOwnerHouses(Long ownerId);

    List<OwnerVO> listAll(Long communityId);

    List<OwnerVO> listUnlinkedOwners();

    void removeById(Long id);

    // ========== 业主端接口 ==========

    OwnerHomeVO getHomeData(Long userId);

    List<OwnerBillVO> listBills(Long userId);

    com.example.spms.model.vo.BillDetailVO getBillDetail(Long userId, Long billId);

    void payBill(Long userId, Long billId, Integer payMethod);

    List<OwnerRepairVO> listRepairs(Long userId);

    OwnerRepairVO getRepairDetail(Long userId, Long orderId);

    void applyRepair(Long userId, RepairApplyRequest request);

    List<OwnerComplaintVO> listComplaints(Long userId);

    void applyComplaint(Long userId, ComplaintApplyRequest request);

    OwnerComplaintVO getComplaintDetail(Long userId, Long complaintId);

    void cancelComplaint(Long userId, Long complaintId);
}
