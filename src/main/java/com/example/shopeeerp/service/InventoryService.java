package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Inventory;

import java.util.List;

/**
 * 库存服务接口
 */
public interface InventoryService {
    int insert(Inventory inventory);
    int deleteById(Long inventoryId);
    int update(Inventory inventory);
    Inventory selectById(Long inventoryId);
    List<Inventory> selectAll();
    List<Inventory> selectByProductId(Long productId);
    List<Inventory> selectByWarehouseId(Long warehouseId);
}
