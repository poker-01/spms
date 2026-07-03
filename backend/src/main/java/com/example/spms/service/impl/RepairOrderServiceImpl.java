package com.example.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.service.RepairOrderService;
import com.example.spms.mapper.RepairOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author poker
* @description 针对表【repair_order(报修工单表)】的数据库操作Service实现
* @createDate 2026-07-03 15:19:38
*/
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder>
    implements RepairOrderService{

}




