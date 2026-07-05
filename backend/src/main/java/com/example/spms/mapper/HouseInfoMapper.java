package com.example.spms.mapper;

import com.example.spms.model.po.HouseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @author poker
 * @description 针对表【house_info(房屋信息表)】的数据库操作Mapper
 * @createDate 2026-07-03 15:19:38
 * @Entity com.example.spms.model.po.HouseInfo
 */
@Mapper
public interface HouseInfoMapper extends BaseMapper<HouseInfo> {

    /**
     * 查询某楼栋所有房屋
     */
    @Select("SELECT * FROM house_info WHERE building_id = #{buildingId} AND is_deleted = 0 ORDER BY house_number")
    List<HouseInfo> selectByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 查询某楼栋已入住或已售的房屋
     */
    @Select("SELECT * FROM house_info WHERE building_id = #{buildingId} AND status IN (1, 2) AND is_deleted = 0 ORDER BY house_number")
    List<HouseInfo> selectActiveByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 查询某业主所有房屋（通过关联表）
     */
    @Select("SELECT h.* FROM house_info h " +
            "INNER JOIN owner_house_rel r ON h.id = r.house_info_id " +
            "WHERE r.owner_info_id = #{ownerId} AND r.is_deleted = 0 AND h.is_deleted = 0")
    List<HouseInfo> selectByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 查询空置房屋
     */
    @Select("SELECT * FROM house_info WHERE status = 0 AND is_deleted = 0 ORDER BY building_id, house_number")
    List<HouseInfo> selectVacantHouses();

    /**
     * 统计某楼栋的房屋数量
     */
    @Select("SELECT COUNT(*) FROM house_info WHERE building_id = #{buildingId} AND is_deleted = 0")
    Long countByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 更新房屋状态
     */
    @Update("UPDATE house_info SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 分页查询房屋（带条件）- 使用XML
     */
    List<HouseInfo> selectPageByCondition(@Param("buildingId") Long buildingId,
                                          @Param("houseNumber") String houseNumber,
                                          @Param("status") Integer status);

    /**
     * 查询房屋带楼栋和小区信息 - 使用XML
     */
    List<HouseInfo> selectWithBuildingInfo(@Param("buildingId") Long buildingId,
                                           @Param("ownerId") Long ownerId);

    /**
     * 统计各状态房屋数量 - 使用XML
     */
    List<Map<String, Object>> countByStatus(@Param("buildingId") Long buildingId);

    /**
     * 根据房屋编号和楼栋ID查询
     */
    @Select("SELECT * FROM house_info WHERE building_id = #{buildingId} AND house_number = #{houseNumber} AND is_deleted = 0")
    HouseInfo selectByNumber(@Param("buildingId") Long buildingId, @Param("houseNumber") String houseNumber);
}