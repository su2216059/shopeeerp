package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OrderItemMapper;
import com.example.shopeeerp.pojo.OrderItem;
import com.example.shopeeerp.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单项服务实现类
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public int insert(OrderItem orderItem) {
        return orderItemMapper.insert(orderItem);
    }

    @Override
    public int deleteById(Long orderItemId) {
        return orderItemMapper.deleteById(orderItemId);
    }

    @Override
    public int update(OrderItem orderItem) {
        return orderItemMapper.update(orderItem);
    }

    @Override
    public OrderItem selectById(Long orderItemId) {
        return orderItemMapper.selectById(orderItemId);
    }

    @Override
    public List<OrderItem> selectAll() {
        return orderItemMapper.selectAll();
    }

    @Override
    public List<OrderItem> selectByOrderId(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderItem> selectByProductId(Long productId) {
        return orderItemMapper.selectByProductId(productId);
    }
}
