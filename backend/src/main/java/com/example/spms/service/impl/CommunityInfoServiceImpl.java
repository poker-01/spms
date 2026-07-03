package com.example.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.model.po.CommunityInfo;
import com.example.spms.service.CommunityInfoService;
import com.example.spms.mapper.CommunityInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author poker
* @description 针对表【community_info(小区信息表)】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
public class CommunityInfoServiceImpl extends ServiceImpl<CommunityInfoMapper, CommunityInfo>
    implements CommunityInfoService{

}




