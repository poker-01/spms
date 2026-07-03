package com.example.spms.mapper;

import com.example.spms.model.po.PaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author poker
* @description 针对表【payment_record(缴费记录表)】的数据库操作Mapper
* @createDate 2026-07-03 15:19:38
* @Entity com.example.spms.model.po.PaymentRecord
*/
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

}




