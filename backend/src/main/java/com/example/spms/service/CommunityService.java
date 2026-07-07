package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.CommunityQueryRequest;
import com.example.spms.model.bo.CommunitySaveRequest;
import com.example.spms.model.bo.CommunityUpdateRequest;
import com.example.spms.model.po.CommunityInfo;
import com.example.spms.model.vo.CommunityVO;

import java.util.List;

public interface CommunityService extends IService<CommunityInfo> {

    Page<CommunityVO> pageQuery(CommunityQueryRequest request, Long communityId);

    void saveCommunity(CommunitySaveRequest request);

    void updateCommunity(CommunityUpdateRequest request);

    CommunityVO getCommunityDetail(Long id);

    List<CommunityVO> listAll(Long communityId);
}