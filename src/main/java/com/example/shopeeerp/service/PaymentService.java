package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Payment;

import java.util.List;

/**
 * 支付服务接口
 */
public interface PaymentService {
    int insert(Payment payment);
    int deleteById(Long paymentId);
    int update(Payment payment);
    Payment selectById(Long paymentId);
    List<Payment> selectAll();
    List<Payment> selectByOrderId(Long orderId);
}
