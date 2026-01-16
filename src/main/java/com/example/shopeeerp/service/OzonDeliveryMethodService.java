package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonDeliveryMethod;

import java.util.List;

public interface OzonDeliveryMethodService {

    OzonDeliveryMethod getById(Long id);

    List<OzonDeliveryMethod> getByWarehouseId(Long warehouseId);

    List<OzonDeliveryMethod> getByWarehouseIds(List<Long> warehouseIds);

    boolean save(OzonDeliveryMethod method);

    boolean saveBatch(List<OzonDeliveryMethod> list);

    boolean update(OzonDeliveryMethod method);

    boolean removeByWarehouseId(Long warehouseId);

    boolean removeByWarehouseIds(List<Long> warehouseIds);
}