package com.example.shopeeerp.adapter.impl;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.model.PlatformCost;
import com.example.shopeeerp.adapter.model.PlatformOrder;
import com.example.shopeeerp.adapter.model.PlatformProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lazada平台适配器实现
 */
@Component
public class LazadaAdapter implements PlatformAdapter {
    
    @Override
    public String getPlatformName() {
        return "Lazada";
    }
    
    @Override
    public List<PlatformOrder> fetchOrders(String startDate, String endDate) {
        // TODO: 实现Lazada API调用
        // 这里应该调用Lazada的API来获取订单数据
        List<PlatformOrder> orders = new ArrayList<>();
        
        // 示例订单
        PlatformOrder order = new PlatformOrder();
        order.setPlatformOrderId("LZ789012");
        order.setCustomerId("C002");
        order.setCustomerName("示例客户2");
        order.setStatus("已发货");
        order.setTotalAmount(new BigDecimal("199.99"));
        order.setPaymentStatus("已支付");
        order.setShippingStatus("已发货");
        order.setOrderDate(LocalDateTime.now());
        
        orders.add(order);
        
        return orders;
    }
    
    @Override
    public List<PlatformProduct> fetchProducts() {
        // TODO: 实现Lazada API调用
        // 这里应该调用Lazada的API来获取产品数据
        List<PlatformProduct> products = new ArrayList<>();
        
        // 示例产品
        PlatformProduct product = new PlatformProduct();
        product.setProductId("P002");
        product.setSku("SKU002");
        product.setName("示例产品2");
        product.setPrice(new BigDecimal("199.99"));
        product.setStock(50);
        
        products.add(product);
        
        return products;
    }
    
    @Override
    public PlatformCost fetchCost(String productId) {
        // TODO: 实现Lazada API调用
        // 这里应该调用Lazada的API来获取成本数据
        PlatformCost cost = new PlatformCost();
        cost.setProductId(productId);
        cost.setCostPrice(new BigDecimal("100.00"));
        cost.setShippingCost(new BigDecimal("15.00"));
        cost.setPlatformFee(new BigDecimal("8.00"));
        cost.setTotalCost(new BigDecimal("123.00"));
        
        return cost;
    }
}
