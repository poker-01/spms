package com.example.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.model.po.BuildingInfo;
import com.example.spms.service.BuildingInfoService;
import com.example.spms.mapper.BuildingInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author poker
* @description 针对表【building_info(楼栋信息表)】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
public class BuildingInfoServiceImpl extends ServiceImpl<BuildingInfoMapper, BuildingInfo>
    implements BuildingInfoService{

}




