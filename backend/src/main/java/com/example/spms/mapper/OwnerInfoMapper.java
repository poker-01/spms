package com.example.spms.mapper;

import com.example.spms.model.po.OwnerInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author poker
 * @description 针对表【owner_info(业主信息表)】的数据库操作Mapper
 * @createDate 2026-07-03 15:19:38
 * @Entity com.example.spms.model.po.OwnerInfo
 */
@Mapper
public interface OwnerInfoMapper extends BaseMapper<OwnerInfo> {

    /**
     * 根据手机号查询业主
     */
    @Select("SELECT * FROM owner_info WHERE owner_phone = #{phone} AND is_deleted = 0")
    OwnerInfo selectByPhone(@Param("phone") String phone);

    /**
     * 根据身份证号查询业主
     */
    @Select("SELECT * FROM owner_info WHERE id_card = #{idCard} AND is_deleted = 0")
    OwnerInfo selectByIdCard(@Param("idCard") String idCard);

    /**
     * 根据房屋ID查询主要业主 - 使用XML
     */
    OwnerInfo selectByHouseId(@Param("houseId") Long houseId);

    /**
     * 查询某房屋所有关联业主
     */
    @Select("SELECT o.* FROM owner_info o " +
            "INNER JOIN owner_house_rel r ON o.id = r.owner_info_id " +
            "WHERE r.house_info_id = #{houseId} AND r.is_deleted = 0 AND o.is_deleted = 0")
    List<OwnerInfo> selectAllByHouseId(@Param("houseId") Long houseId);

    /**
     * 模糊查询业主姓名
     */
    @Select("SELECT * FROM owner_info WHERE owner_name LIKE CONCAT('%', #{name}, '%') AND is_deleted = 0")
    List<OwnerInfo> selectByNameLike(@Param("name") String name);

    /**
     * 查询启用的业主列表
     */
    @Select("SELECT * FROM owner_info WHERE status = 1 AND is_deleted = 0 ORDER BY owner_name")
    List<OwnerInfo> selectActiveList();

    /**
     * 分页查询业主（带条件）- 使用XML
     */
    List<OwnerInfo> selectPageByCondition(@Param("ownerName") String ownerName,
                                          @Param("ownerPhone") String ownerPhone,
                                          @Param("status") Integer status);

    /**
     * 查询业主详情（含关联房屋）- 使用XML
     */
    OwnerInfo selectDetailById(@Param("ownerId") Long ownerId);

    /**
     * 统计各性别业主数量
     */
    @Select("SELECT gender, COUNT(*) AS count FROM owner_info WHERE is_deleted = 0 GROUP BY gender")
    List<Map<String, Object>> countGroupByGender();
}