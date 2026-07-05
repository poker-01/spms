package com.example.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spms.model.po.ComplaintSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 投诉建议Mapper
 *
 * @author SPMS
 * @date 2026/07/05
 */
@Mapper
public interface ComplaintSuggestionMapper extends BaseMapper<ComplaintSuggestion> {

    /**
     * 分页查询投诉建议（关联业主、房屋信息）
     */
    IPage<Map<String, Object>> selectComplaintPage(Page<?> page,
                                                    @Param("complaintNo") String complaintNo,
                                                    @Param("status") Integer status,
                                                    @Param("type") String type,
                                                    @Param("ownerId") Long ownerId,
                                                    @Param("startTime") String startTime,
                                                    @Param("endTime") String endTime);

    /**
     * 查询投诉建议详情
     */
    Map<String, Object> selectComplaintDetail(@Param("complaintId") Long complaintId);

    /**
     * 统计各状态投诉数量
     */
    @Select("SELECT status, COUNT(*) as count FROM complaint_suggestion WHERE is_deleted = 0 GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计各类投诉数量
     */
    @Select("SELECT type, COUNT(*) as count FROM complaint_suggestion WHERE is_deleted = 0 GROUP BY type")
    List<Map<String, Object>> countByType();

    /**
     * 更新投诉状态
     */
    @Update("UPDATE complaint_suggestion SET status = #{status}, update_time = NOW() WHERE id = #{complaintId}")
    int updateStatus(@Param("complaintId") Long complaintId, @Param("status") Integer status);
}