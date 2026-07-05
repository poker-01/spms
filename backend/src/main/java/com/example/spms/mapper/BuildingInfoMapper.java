package com.example.spms.mapper;

import com.example.spms.model.po.BuildingInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author poker
 * @description 针对表【building_info(楼栋信息表)】的数据库操作Mapper
 * @createDate 2026-07-03 15:19:38
 * @Entity com.example.spms.model.po.BuildingInfo
 */
@Mapper
public interface BuildingInfoMapper extends BaseMapper<BuildingInfo> {

    /**
     * 查询某小区所有楼栋
     */
    @Select("SELECT * FROM building_info WHERE community_id = #{communityId} AND is_deleted = 0 ORDER BY building_code")
    List<BuildingInfo> selectByCommunityId(@Param("communityId") Long communityId);

    /**
     * 查询某小区启用的楼栋
     */
    @Select("SELECT * FROM building_info WHERE community_id = #{communityId} AND status = 1 AND is_deleted = 0 ORDER BY building_code")
    List<BuildingInfo> selectActiveByCommunityId(@Param("communityId") Long communityId);

    /**
     * 统计某小区的楼栋数量
     */
    @Select("SELECT COUNT(*) FROM building_info WHERE community_id = #{communityId} AND is_deleted = 0")
    Long countByCommunityId(@Param("communityId") Long communityId);

    /**
     * 分页查询楼栋（带条件）- 使用XML
     */
    List<BuildingInfo> selectPageByCondition(@Param("communityId") Long communityId,
                                             @Param("buildingName") String buildingName,
                                             @Param("status") Integer status);

    /**
     * 查询楼栋带小区名称 - 使用XML
     */
    List<BuildingInfo> selectWithCommunityName(@Param("communityId") Long communityId);

    /**
     * 查询楼栋及其房屋数量 - 使用XML
     */
    List<Map<String, Object>> selectWithHouseCount(@Param("communityId") Long communityId);

    /**
     * 根据楼栋编码和小区ID查询
     */
    @Select("SELECT * FROM building_info WHERE community_id = #{communityId} AND building_code = #{buildingCode} AND is_deleted = 0")
    BuildingInfo selectByCode(@Param("communityId") Long communityId, @Param("buildingCode") String buildingCode);
}