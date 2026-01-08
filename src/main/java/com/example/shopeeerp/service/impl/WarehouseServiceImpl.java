package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.WarehouseMapper;
import com.example.shopeeerp.pojo.Warehouse;
import com.example.shopeeerp.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仓库服务实现类
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public int insert(Warehouse warehouse) {
        return warehouseMapper.insert(warehouse);
    }

    @Override
    public int deleteById(Long warehouseId) {
        return warehouseMapper.deleteById(warehouseId);
    }

    @Override
    public int update(Warehouse warehouse) {
        return warehouseMapper.update(warehouse);
    }

    @Override
    public Warehouse selectById(Long warehouseId) {
        return warehouseMapper.selectById(warehouseId);
    }

    @Override
    public List<Warehouse> selectAll() {
        return warehouseMapper.selectAll();
    }
}
