package com.example.spms.mapper;

import com.example.spms.model.po.FeeItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【fee_item(费用项目表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.FeeItem
*/
@Mapper
public interface FeeItemMapper extends BaseMapper<FeeItem> {

}




