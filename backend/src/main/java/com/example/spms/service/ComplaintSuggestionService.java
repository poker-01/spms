package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.ComplaintApplyRequest;
import com.example.spms.model.bo.ComplaintQueryRequest;
import com.example.spms.model.bo.ComplaintReplyRequest;
import com.example.spms.model.po.ComplaintSuggestion;
import com.example.spms.model.vo.ComplaintDetailVO;
import com.example.spms.model.vo.ComplaintVO;

/**
 * 投诉建议服务接口
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface ComplaintSuggestionService extends IService<ComplaintSuggestion> {

    /**
     * 分页查询投诉建议
     */
    Page<ComplaintVO> pageComplaints(ComplaintQueryRequest request);

    /**
     * 获取投诉建议详情
     */
    ComplaintDetailVO getComplaintDetail(Long complaintId);

    /**
     * 提交投诉建议（业主端）
     */
    void submitComplaint(Long userId, ComplaintApplyRequest request);

    /**
     * 回复投诉建议（物业端）
     */
    void replyComplaint(ComplaintReplyRequest request);

    /**
     * 关闭投诉
     */
    void closeComplaint(Long complaintId);

    /**
     * 统计各状态投诉数量
     */
    Object countByStatus();

    /**
     * 统计各类投诉数量
     */
    Object countByType();
}