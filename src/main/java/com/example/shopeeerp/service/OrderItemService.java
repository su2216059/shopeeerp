package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OrderItem;

import java.util.List;

/**
 * 订单项服务接口
 */
public interface OrderItemService {
    int insert(OrderItem orderItem);
    int deleteById(Long orderItemId);
    int update(OrderItem orderItem);
    OrderItem selectById(Long orderItemId);
    List<OrderItem> selectAll();
    List<OrderItem> selectByOrderId(Long orderId);
    List<OrderItem> selectByProductId(Long productId);
}
