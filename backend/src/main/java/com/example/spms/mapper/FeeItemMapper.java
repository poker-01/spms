package com.example.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spms.model.po.FeeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 费用项目Mapper
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Mapper
public interface FeeItemMapper extends BaseMapper<FeeItem> {

    /**
     * 分页查询费用项目
     */
    IPage<Map<String, Object>> selectFeeItemPage(Page<?> page,
                                                   @Param("itemCode") String itemCode,
                                                   @Param("itemName") String itemName,
                                                   @Param("itemType") Integer itemType,
                                                   @Param("status") Integer status);

    /**
     * 检查编码是否存在
     */
    @Select("SELECT COUNT(*) FROM fee_item WHERE item_code = #{itemCode} AND is_deleted = 0")
    int countByCode(@Param("itemCode") String itemCode);

    /**
     * 检查编码是否存在（排除自身）
     */
    @Select("SELECT COUNT(*) FROM fee_item WHERE item_code = #{itemCode} AND is_deleted = 0 AND id != #{id}")
    int countByCodeExcludeSelf(@Param("itemCode") String itemCode, @Param("id") Long id);
}