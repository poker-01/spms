package com.example.spms.service;

import com.example.spms.model.vo.DashboardStatsVO;

/**
 * 数据统计看板服务
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface DashboardService {

    /**
     * 获取全局统计数据
     *
     * @param communityId 小区ID，为 null 时统计全部
     */
    DashboardStatsVO getStats(Long communityId);
}
