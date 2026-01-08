package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Warehouse;

import java.util.List;

/**
 * 仓库服务接口
 */
public interface WarehouseService {
    int insert(Warehouse warehouse);
    int deleteById(Long warehouseId);
    int update(Warehouse warehouse);
    Warehouse selectById(Long warehouseId);
    List<Warehouse> selectAll();
}
