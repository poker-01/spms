package com.example.spms.mapper;

import com.example.spms.model.po.CommunityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author poker
 * @description 针对表【community_info(小区信息表)】的数据库操作Mapper
 * @createDate 2026-07-03 15:19:38
 * @Entity com.example.spms.model.po.CommunityInfo
 */
@Mapper
public interface CommunityInfoMapper extends BaseMapper<CommunityInfo> {

    /**
     * 根据小区名称模糊查询
     */
    @Select("SELECT * FROM community_info WHERE community_name LIKE CONCAT('%', #{name}, '%') AND is_deleted = 0")
    List<CommunityInfo> selectByNameLike(@Param("name") String name);

    /**
     * 查询启用的小区列表
     */
    @Select("SELECT * FROM community_info WHERE status = 1 AND is_deleted = 0 ORDER BY community_name")
    List<CommunityInfo> selectActiveList();

    /**
     * 统计小区总数
     */
    @Select("SELECT COUNT(*) FROM community_info WHERE is_deleted = 0")
    Long countActive();

    /**
     * 分页查询小区（带条件）- 使用XML
     */
    List<CommunityInfo> selectPageByCondition(@Param("communityName") String communityName,
                                              @Param("city") String city,
                                              @Param("status") Integer status);

    /**
     * 查询小区及其楼栋数量、房屋数量
     */
    List<Map<String, Object>> selectWithBuildingCount();

    /**
     * 根据城市分组统计小区数量
     */
    @Select("SELECT city, COUNT(*) AS count FROM community_info WHERE is_deleted = 0 GROUP BY city")
    List<Map<String, Object>> countGroupByCity();
}