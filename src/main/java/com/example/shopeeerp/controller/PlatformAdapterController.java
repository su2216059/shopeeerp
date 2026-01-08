package com.example.shopeeerp.controller;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.PlatformAdapterFactory;
import com.example.shopeeerp.adapter.model.PlatformCost;
import com.example.shopeeerp.adapter.model.PlatformOrder;
import com.example.shopeeerp.adapter.model.PlatformProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台适配器控制器
 * 提供统一的API接口来访问不同电商平台的数据
 */
@RestController
@RequestMapping("/api/platform")
@CrossOrigin(origins = "*")
public class PlatformAdapterController {
    
    @Autowired
    private PlatformAdapterFactory adapterFactory;
    
    /**
     * 获取所有支持的平台
     */
    @GetMapping("/platforms")
    public ResponseEntity<Map<String, Object>> getSupportedPlatforms() {
        Map<String, Object> result = new HashMap<>();
        result.put("platforms", adapterFactory.getSupportedPlatforms());
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取指定平台的订单
     */
    @GetMapping("/{platform}/orders")
    public ResponseEntity<List<PlatformOrder>> getOrders(
            @PathVariable String platform,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            PlatformAdapter adapter = adapterFactory.getAdapter(platform);
            List<PlatformOrder> orders = adapter.fetchOrders(startDate, endDate);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取指定平台的产品
     */
    @GetMapping("/{platform}/products")
    public ResponseEntity<List<PlatformProduct>> getProducts(@PathVariable String platform) {
        try {
            PlatformAdapter adapter = adapterFactory.getAdapter(platform);
            List<PlatformProduct> products = adapter.fetchProducts();
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取指定平台的产品成本
     */
    @GetMapping("/{platform}/costs/{productId}")
    public ResponseEntity<PlatformCost> getCost(
            @PathVariable String platform,
            @PathVariable String productId) {
        try {
            PlatformAdapter adapter = adapterFactory.getAdapter(platform);
            PlatformCost cost = adapter.fetchCost(productId);
            return ResponseEntity.ok(cost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
