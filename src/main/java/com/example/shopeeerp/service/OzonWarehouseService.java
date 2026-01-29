package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonWarehouse;

import java.util.List;

public interface OzonWarehouseService {

    OzonWarehouse getByWarehouseId(Long warehouseId, Long shopId);

    List<OzonWarehouse> getAll(Long shopId);

    boolean save(OzonWarehouse warehouse);

    boolean update(OzonWarehouse warehouse);

    boolean saveBatch(List<OzonWarehouse> warehouses);

    boolean removeByWarehouseId(Long warehouseId, Long shopId);
}
