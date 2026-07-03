package com.example.spms.mapper;

import com.example.spms.model.po.BillInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【bill_info(账单信息表)】的数据库操作Mapper
* @createDate 2026-07-03 15:15:41
* @Entity com.example.spms.model.po.BillInfo
*/
@Mapper
public interface BillInfoMapper extends BaseMapper<BillInfo> {

}




