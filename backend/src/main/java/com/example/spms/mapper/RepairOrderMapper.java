package com.example.spms.mapper;

import com.example.spms.model.po.RepairOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【repair_order(报修工单表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.RepairOrder
*/
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

}




