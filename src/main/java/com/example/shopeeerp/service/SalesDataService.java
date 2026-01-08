package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.SalesData;

import java.util.List;

/**
 * 销售数据服务接口
 */
public interface SalesDataService {
    int insert(SalesData salesData);
    int deleteById(Long salesId);
    int update(SalesData salesData);
    SalesData selectById(Long salesId);
    List<SalesData> selectAll();
    List<SalesData> selectByProductId(Long productId);
    List<SalesData> selectByOrderId(Long orderId);
}
