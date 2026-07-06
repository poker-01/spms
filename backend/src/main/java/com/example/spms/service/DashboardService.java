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
     */
    DashboardStatsVO getStats();
}
