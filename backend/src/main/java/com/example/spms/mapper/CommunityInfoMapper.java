package com.example.spms.mapper;

import com.example.spms.model.po.CommunityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【community_info(小区信息表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.CommunityInfo
*/
@Mapper
public interface CommunityInfoMapper extends BaseMapper<CommunityInfo> {

}




