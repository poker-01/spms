package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.HouseQueryRequest;
import com.example.spms.model.bo.HouseSaveRequest;
import com.example.spms.model.bo.HouseUpdateRequest;
import com.example.spms.model.po.HouseInfo;
import com.example.spms.model.vo.HouseVO;

import java.util.List;

public interface HouseService extends IService<HouseInfo> {

    /**
     * 分页查询房屋
     */
    Page<HouseVO> pageQuery(HouseQueryRequest request, Long communityId);

    /**
     * 新增房屋
     */
    void saveHouse(HouseSaveRequest request);

    /**
     * 修改房屋
     */
    void updateHouse(HouseUpdateRequest request);

    /**
     * 查询房屋详情
     */
    HouseVO getHouseDetail(Long id);

    /**
     * 查询某楼栋所有房屋（关联查询）
     */
    List<HouseVO> listByBuildingId(Long buildingId);

    /**
     * 查询某业主所有房屋
     */
    List<HouseVO> listByOwnerId(Long ownerId);

    /**
     * 查询某楼栋下未被占用的房屋（新增业主时选择用）
     */
    List<HouseVO> listAvailableByBuildingId(Long buildingId);

    /**
     * 查询全部房屋（下拉列表用）
     */
    List<HouseVO> listAll(Long communityId);
}