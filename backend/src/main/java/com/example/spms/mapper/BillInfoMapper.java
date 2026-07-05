package com.example.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spms.model.po.BillInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 账单信息Mapper
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Mapper
public interface BillInfoMapper extends BaseMapper<BillInfo> {

    /**
     * 分页查询账单（关联业主、房屋、费用项目信息）
     */
    IPage<Map<String, Object>> selectBillPage(Page<?> page,
                                               @Param("billNo") String billNo,
                                               @Param("ownerId") Long ownerId,
                                               @Param("houseId") Long houseId,
                                               @Param("feeItemId") Long feeItemId,
                                               @Param("status") Integer status,
                                               @Param("billPeriod") String billPeriod,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);

    /**
     * 查询账单详情
     */
    Map<String, Object> selectBillDetail(@Param("billId") Long billId);

    /**
     * 更新已付金额
     */
    @Update("UPDATE bill_info SET paid_amount = paid_amount + #{amount}, " +
            "status = CASE WHEN paid_amount + #{amount} >= bill_amount THEN 2 ELSE 1 END, " +
            "pay_time = CASE WHEN paid_amount + #{amount} >= bill_amount THEN NOW() ELSE pay_time END, " +
            "update_time = NOW() " +
            "WHERE id = #{billId} AND is_deleted = 0")
    int updatePaidAmount(@Param("billId") Long billId, @Param("amount") BigDecimal amount);

    /**
     * 统计各状态账单数量
     */
    @Select("SELECT status, COUNT(*) as count FROM bill_info WHERE is_deleted = 0 GROUP BY status")
    java.util.List<Map<String, Object>> countByStatus();

    /**
     * 统计总欠费金额
     */
    @Select("SELECT SUM(bill_amount - paid_amount) as total FROM bill_info " +
            "WHERE is_deleted = 0 AND status IN (0, 1)")
    BigDecimal sumOverdueAmount();
}