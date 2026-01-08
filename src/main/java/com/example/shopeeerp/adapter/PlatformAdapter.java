package com.example.shopeeerp.adapter;

import com.example.shopeeerp.adapter.model.PlatformCost;
import com.example.shopeeerp.adapter.model.PlatformOrder;
import com.example.shopeeerp.adapter.model.PlatformProduct;

import java.util.List;

/**
 * 平台适配器接口
 * 用于统一不同电商平台的API调用
 */
public interface PlatformAdapter {
    
    /**
     * 获取平台名称
     * @return 平台名称（如：Shopee、Lazada）
     */
    String getPlatformName();
    
    /**
     * 获取订单列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单列表
     */
    List<PlatformOrder> fetchOrders(String startDate, String endDate);
    
    /**
     * 获取产品列表
     * @return 产品列表
     */
    List<PlatformProduct> fetchProducts();
    
    /**
     * 获取成本信息
     * @param productId 产品ID
     * @return 成本信息
     */
    PlatformCost fetchCost(String productId);
}
