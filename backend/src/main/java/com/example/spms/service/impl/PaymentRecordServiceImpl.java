package com.example.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.model.po.PaymentRecord;
import com.example.spms.service.PaymentRecordService;
import com.example.spms.mapper.PaymentRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author poker
* @description 针对表【payment_record(缴费记录表)】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord>
    implements PaymentRecordService{

}




