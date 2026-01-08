package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.InventoryMapper;
import com.example.shopeeerp.pojo.Inventory;
import com.example.shopeeerp.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存服务实现类
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public int insert(Inventory inventory) {
        return inventoryMapper.insert(inventory);
    }

    @Override
    public int deleteById(Long inventoryId) {
        return inventoryMapper.deleteById(inventoryId);
    }

    @Override
    public int update(Inventory inventory) {
        return inventoryMapper.update(inventory);
    }

    @Override
    public Inventory selectById(Long inventoryId) {
        return inventoryMapper.selectById(inventoryId);
    }

    @Override
    public List<Inventory> selectAll() {
        return inventoryMapper.selectAll();
    }

    @Override
    public List<Inventory> selectByProductId(Long productId) {
        return inventoryMapper.selectByProductId(productId);
    }

    @Override
    public List<Inventory> selectByWarehouseId(Long warehouseId) {
        return inventoryMapper.selectByWarehouseId(warehouseId);
    }
}
