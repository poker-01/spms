package com.example.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spms.model.po.InspectionRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 巡检记录Mapper（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface InspectionMapper extends BaseMapper<InspectionRecord> {

    /**
     * 分页查询巡检记录
     */
    IPage<Map<String, Object>> selectInspectionPage(Page<?> page,
                                                     @Param("location") String location,
                                                     @Param("result") Integer result,
                                                     @Param("inspectorId") Long inspectorId,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime);
}