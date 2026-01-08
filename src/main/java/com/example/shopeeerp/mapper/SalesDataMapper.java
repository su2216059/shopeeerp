package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.SalesData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售数据Mapper接口
 */
@Mapper
public interface SalesDataMapper {
    /**
     * 插入销售数据记录
     */
    int insert(SalesData salesData);

    /**
     * 根据ID删除销售数据记录
     */
    int deleteById(Long salesId);

    /**
     * 更新销售数据记录
     */
    int update(SalesData salesData);

    /**
     * 根据ID查询销售数据记录
     */
    SalesData selectById(Long salesId);

    /**
     * 查询所有销售数据记录
     */
    List<SalesData> selectAll();

    /**
     * 根据产品ID查询销售数据记录
     */
    List<SalesData> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据订单ID查询销售数据记录
     */
    List<SalesData> selectByOrderId(@Param("orderId") Long orderId);
}
