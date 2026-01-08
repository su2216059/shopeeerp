package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单项Mapper接口
 */
@Mapper
public interface OrderItemMapper {
    /**
     * 插入订单项记录
     */
    int insert(OrderItem orderItem);

    /**
     * 根据ID删除订单项记录
     */
    int deleteById(Long orderItemId);

    /**
     * 更新订单项记录
     */
    int update(OrderItem orderItem);

    /**
     * 根据ID查询订单项记录
     */
    OrderItem selectById(Long orderItemId);

    /**
     * 查询所有订单项记录
     */
    List<OrderItem> selectAll();

    /**
     * 根据订单ID查询订单项记录
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据产品ID查询订单项记录
     */
    List<OrderItem> selectByProductId(@Param("productId") Long productId);
}
