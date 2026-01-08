package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单记录
     */
    int insert(Order order);

    /**
     * 根据ID删除订单记录
     */
    int deleteById(Long orderId);

    /**
     * 更新订单记录
     */
    int update(Order order);

    /**
     * 根据ID查询订单记录
     */
    Order selectById(Long orderId);

    /**
     * 查询所有订单记录
     */
    List<Order> selectAll();

    /**
     * 根据客户ID查询订单记录
     */
    List<Order> selectByCustomerId(@Param("customerId") Long customerId);
}
