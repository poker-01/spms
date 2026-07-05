package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerVO;

import java.util.List;

public interface OwnerService extends IService<OwnerInfo> {

    /**
     * 分页查询业主
     */
    Page<OwnerVO> pageQuery(OwnerQueryRequest request);

    /**
     * 新增业主
     */
    void saveOwner(OwnerSaveRequest request);

    /**
     * 修改业主
     */
    void updateOwner(OwnerUpdateRequest request);

    /**
     * 查询业主详情
     */
    OwnerVO getOwnerDetail(Long id);

    /**
     * 根据房屋ID查询业主
     */
    OwnerVO getOwnerByHouseId(Long houseId);

    /**
     * 查询业主关联的房屋列表
     */
    List<OwnerHouseRelVO> listOwnerHouses(Long ownerId);

    /**
     * 查询全部业主（下拉列表用）
     */
    List<OwnerVO> listAll();
}