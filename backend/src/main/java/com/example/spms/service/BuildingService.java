package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.BuildingQueryRequest;
import com.example.spms.model.bo.BuildingSaveRequest;
import com.example.spms.model.bo.BuildingUpdateRequest;
import com.example.spms.model.po.BuildingInfo;
import com.example.spms.model.vo.BuildingVO;

import java.util.List;

public interface BuildingService extends IService<BuildingInfo> {

    Page<BuildingVO> pageQuery(BuildingQueryRequest request);

    void saveBuilding(BuildingSaveRequest request);

    void updateBuilding(BuildingUpdateRequest request);

    BuildingVO getBuildingDetail(Long id);

    List<BuildingVO> listByCommunityId(Long communityId);

    List<BuildingVO> listAll();
}