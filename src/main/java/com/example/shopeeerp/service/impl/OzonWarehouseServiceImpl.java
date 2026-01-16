package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonWarehouseMapper;
import com.example.shopeeerp.pojo.OzonWarehouse;
import com.example.shopeeerp.service.OzonWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OzonWarehouseServiceImpl implements OzonWarehouseService {

    @Autowired
    private OzonWarehouseMapper mapper;

    @Override
    public OzonWarehouse getByWarehouseId(Long warehouseId) {
        if (warehouseId == null) {
            return null;
        }
        return mapper.selectByWarehouseId(warehouseId);
    }

    @Override
    public List<OzonWarehouse> getAll() {
        List<OzonWarehouse> list = mapper.selectAll();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public boolean save(OzonWarehouse warehouse) {
        if (warehouse == null) {
            return false;
        }
        return mapper.insert(warehouse) > 0;
    }

    @Override
    public boolean update(OzonWarehouse warehouse) {
        if (warehouse == null) {
            return false;
        }
        return mapper.updateByWarehouseId(warehouse) > 0;
    }

    @Override
    public boolean saveBatch(List<OzonWarehouse> warehouses) {
        if (warehouses == null || warehouses.isEmpty()) {
            return true;
        }
        int count = 0;
        for (OzonWarehouse warehouse : warehouses) {
            if (warehouse == null) {
                continue;
            }
            count += mapper.insert(warehouse);
        }
        return count > 0;
    }

    @Override
    public boolean removeByWarehouseId(Long warehouseId) {
        if (warehouseId == null) {
            return false;
        }
        return mapper.deleteByWarehouseId(warehouseId) > 0;
    }
}