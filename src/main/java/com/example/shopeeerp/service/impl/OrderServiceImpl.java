package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OrderMapper;
import com.example.shopeeerp.pojo.Order;
import com.example.shopeeerp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public int insert(Order order) {
        return orderMapper.insert(order);
    }

    @Override
    public int deleteById(Long orderId) {
        return orderMapper.deleteById(orderId);
    }

    @Override
    public int update(Order order) {
        return orderMapper.update(order);
    }

    @Override
    public Order selectById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public List<Order> selectAll() {
        return orderMapper.selectAll();
    }

    @Override
    public List<Order> selectByCustomerId(Long customerId) {
        return orderMapper.selectByCustomerId(customerId);
    }
}
