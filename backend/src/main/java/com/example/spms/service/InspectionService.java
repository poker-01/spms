package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.InspectionQueryRequest;
import com.example.spms.model.bo.InspectionSaveRequest;
import com.example.spms.model.po.InspectionRecord;
import com.example.spms.model.vo.InspectionVO;

/**
 * 巡检记录服务接口（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface InspectionService extends IService<InspectionRecord> {

    /**
     * 分页查询巡检记录
     */
    Page<InspectionVO> pageInspections(InspectionQueryRequest request);

    /**
     * 新增巡检记录
     */
    void saveInspection(Long userId, InspectionSaveRequest request);
}