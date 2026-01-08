package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付Mapper接口
 */
@Mapper
public interface PaymentMapper {
    /**
     * 插入支付记录
     */
    int insert(Payment payment);

    /**
     * 根据ID删除支付记录
     */
    int deleteById(Long paymentId);

    /**
     * 更新支付记录
     */
    int update(Payment payment);

    /**
     * 根据ID查询支付记录
     */
    Payment selectById(Long paymentId);

    /**
     * 查询所有支付记录
     */
    List<Payment> selectAll();

    /**
     * 根据订单ID查询支付记录
     */
    List<Payment> selectByOrderId(@Param("orderId") Long orderId);
}
