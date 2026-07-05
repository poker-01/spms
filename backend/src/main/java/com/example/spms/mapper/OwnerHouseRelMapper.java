package com.example.spms.mapper;

import com.example.spms.model.po.OwnerHouseRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author poker
 * @description 针对表【owner_house_rel(业主房屋关联表)】的数据库操作Mapper
 * @createDate 2026-07-03 15:19:38
 * @Entity com.example.spms.model.po.OwnerHouseRel
 */
@Mapper
public interface OwnerHouseRelMapper extends BaseMapper<OwnerHouseRel> {

    /**
     * 查询业主所有关联房屋ID
     */
    @Select("SELECT house_info_id FROM owner_house_rel WHERE owner_info_id = #{ownerId} AND is_deleted = 0")
    List<Long> selectHouseIdsByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 查询房屋所有关联业主ID
     */
    @Select("SELECT owner_info_id FROM owner_house_rel WHERE house_info_id = #{houseId} AND is_deleted = 0")
    List<Long> selectOwnerIdsByHouseId(@Param("houseId") Long houseId);

    /**
     * 查询业主主要房屋ID
     */
    @Select("SELECT house_info_id FROM owner_house_rel " +
            "WHERE owner_info_id = #{ownerId} AND is_primary = 1 AND is_deleted = 0")
    Long selectPrimaryHouseId(@Param("ownerId") Long ownerId);

    /**
     * 删除业主所有房屋关联（软删除）
     */
    @Update("UPDATE owner_house_rel SET is_deleted = 1 WHERE owner_info_id = #{ownerId}")
    int deleteByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 删除房屋所有业主关联（软删除）
     */
    @Update("UPDATE owner_house_rel SET is_deleted = 1 WHERE house_info_id = #{houseId}")
    int deleteByHouseId(@Param("houseId") Long houseId);

    /**
     * 查询业主关联的房屋详情 - 使用XML
     */
    List<OwnerHouseRel> selectOwnerHouseDetail(@Param("ownerId") Long ownerId,
                                               @Param("houseId") Long houseId);

    /**
     * 批量插入关联关系 - 使用XML
     */
    int batchInsert(@Param("list") List<OwnerHouseRel> list);

    /**
     * 清除主要房屋标识
     */
    @Update("UPDATE owner_house_rel SET is_primary = 0 WHERE owner_info_id = #{ownerId} AND is_deleted = 0")
    int clearPrimaryFlag(@Param("ownerId") Long ownerId);

    /**
     * 设置主要房屋
     */
    @Update("UPDATE owner_house_rel SET is_primary = 1 " +
            "WHERE owner_info_id = #{ownerId} AND house_info_id = #{houseId} AND is_deleted = 0")
    int setPrimaryHouse(@Param("ownerId") Long ownerId, @Param("houseId") Long houseId);

    /**
     * 检查关联是否存在
     */
    @Select("SELECT COUNT(*) FROM owner_house_rel " +
            "WHERE owner_info_id = #{ownerId} AND house_info_id = #{houseId} AND is_deleted = 0")
    int exists(@Param("ownerId") Long ownerId, @Param("houseId") Long houseId);
}