package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    int insert(Order order);
    int deleteById(Long orderId, Long shopId);
    int update(Order order);
    Order selectById(Long orderId, Long shopId);
    List<Order> selectAll(Long shopId);
    List<Order> selectByCustomerId(Long customerId, Long shopId);
}
