package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.PaymentMapper;
import com.example.shopeeerp.pojo.Payment;
import com.example.shopeeerp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public int insert(Payment payment) {
        return paymentMapper.insert(payment);
    }

    @Override
    public int deleteById(Long paymentId) {
        return paymentMapper.deleteById(paymentId);
    }

    @Override
    public int update(Payment payment) {
        return paymentMapper.update(payment);
    }

    @Override
    public Payment selectById(Long paymentId) {
        return paymentMapper.selectById(paymentId);
    }

    @Override
    public List<Payment> selectAll() {
        return paymentMapper.selectAll();
    }

    @Override
    public List<Payment> selectByOrderId(Long orderId) {
        return paymentMapper.selectByOrderId(orderId);
    }
}
