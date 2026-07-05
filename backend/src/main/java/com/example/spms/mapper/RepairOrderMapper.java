package com.example.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spms.model.po.RepairOrder;
import com.example.spms.model.vo.DashboardStatsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 报修工单Mapper
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 分页查询报修工单（关联业主、房屋、维修人员信息）
     */
    IPage<Map<String, Object>> selectRepairOrderPage(Page<?> page,
                                                     @Param("orderNo") String orderNo,
                                                     @Param("status") Integer status,
                                                     @Param("repairType") Integer repairType,
                                                     @Param("ownerId") Long ownerId,
                                                     @Param("assigneeId") Long assigneeId,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime);

    /**
     * 查询报修工单详情
     */
    Map<String, Object> selectRepairOrderDetail(@Param("orderId") Long orderId);

    /**
     * 统计各状态报修数量
     */
    @Select("SELECT status, COUNT(*) as count FROM repair_order WHERE is_deleted = 0 GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计各类报修数量
     */
    @Select("SELECT repair_type, COUNT(*) as count FROM repair_order WHERE is_deleted = 0 GROUP BY repair_type")
    List<Map<String, Object>> countByRepairType();

    /**
     * 查询近7日报修趋势
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as total, " +
            "SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as completed " +
            "FROM repair_order " +
            "WHERE is_deleted = 0 AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time)")
    List<Map<String, Object>> selectDailyRepairStats();

    /**
     * 计算平均处理时长（已完成工单）
     */
    @Select("SELECT AVG(TIMESTAMPDIFF(HOUR, create_time, repair_time)) as avg_hours " +
            "FROM repair_order " +
            "WHERE is_deleted = 0 AND status = 3 AND repair_time IS NOT NULL")
    Double selectAvgProcessHours();

    /**
     * 更新工单状态
     */
    @Update("UPDATE repair_order SET status = #{status}, update_time = NOW() WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
}