package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.RepairAssignRequest;
import com.example.spms.model.bo.RepairCompleteRequest;
import com.example.spms.model.bo.RepairEvaluateRequest;
import com.example.spms.model.bo.RepairQueryRequest;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.model.vo.RepairDetailVO;
import com.example.spms.model.vo.RepairOrderVO;

/**
 * 报修工单服务接口
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface RepairOrderService extends IService<RepairOrder> {

    /**
     * 分页查询报修工单（支持按小区过滤）
     */
    Page<RepairOrderVO> pageRepairs(RepairQueryRequest request, Long communityId);

    /**
     * 获取报修工单详情
     */
    RepairDetailVO getRepairDetail(Long orderId);

    /**
     * 派单
     */
    void assignRepair(RepairAssignRequest request);

    /**
     * 开始处理
     */
    void startProcess(Long orderId);

    /**
     * 完成维修
     */
    void completeRepair(RepairCompleteRequest request);

    /**
     * 评价
     */
    void evaluateRepair(RepairEvaluateRequest request);

    /**
     * 取消工单
     */
    void cancelRepair(Long orderId);

    /**
     * 统计各状态工单数量
     */
    Object countByStatus();
}