package com.example.spms.mapper;

import com.example.spms.model.po.OwnerInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【owner_info(业主信息表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.OwnerInfo
*/
@Mapper
public interface OwnerInfoMapper extends BaseMapper<OwnerInfo> {

}




