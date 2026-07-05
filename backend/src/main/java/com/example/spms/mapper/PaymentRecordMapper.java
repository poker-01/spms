package com.example.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spms.model.po.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 缴费记录Mapper
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * 分页查询缴费记录
     */
    IPage<Map<String, Object>> selectPaymentRecordPage(Page<?> page,
                                                         @Param("paymentNo") String paymentNo,
                                                         @Param("billId") Long billId,
                                                         @Param("ownerId") Long ownerId,
                                                         @Param("houseId") Long houseId,
                                                         @Param("payMethod") Integer payMethod,
                                                         @Param("startTime") String startTime,
                                                         @Param("endTime") String endTime);

    /**
     * 查询账单的缴费记录
     */
    @Select("SELECT p.*, o.owner_name, u.full_name as operator_name " +
            "FROM payment_record p " +
            "LEFT JOIN owner_info o ON p.owner_id = o.id AND o.is_deleted = 0 " +
            "LEFT JOIN sys_user_info u ON p.operator_id = u.id AND u.is_deleted = 0 " +
            "WHERE p.bill_id = #{billId} AND p.is_deleted = 0 " +
            "ORDER BY p.pay_time DESC")
    java.util.List<Map<String, Object>> selectByBillId(@Param("billId") Long billId);

    /**
     * 统计总缴费金额
     */
    @Select("SELECT SUM(pay_amount) as total FROM payment_record WHERE is_deleted = 0")
    java.math.BigDecimal sumTotalPayAmount();
}