package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonDeliveryMethodMapper;
import com.example.shopeeerp.pojo.OzonDeliveryMethod;
import com.example.shopeeerp.service.OzonDeliveryMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OzonDeliveryMethodServiceImpl implements OzonDeliveryMethodService {

    @Autowired
    private OzonDeliveryMethodMapper mapper;

    @Override
    public OzonDeliveryMethod getById(Long id) {
        if (id == null) {
            return null;
        }
        return mapper.selectById(id);
    }

    @Override
    public List<OzonDeliveryMethod> getByWarehouseId(Long warehouseId) {
        if (warehouseId == null) {
            return Collections.emptyList();
        }
        List<OzonDeliveryMethod> list = mapper.selectByWarehouseId(warehouseId);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<OzonDeliveryMethod> getByWarehouseIds(List<Long> warehouseIds) {
        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<OzonDeliveryMethod> list = mapper.selectByWarehouseIds(warehouseIds);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public boolean save(OzonDeliveryMethod method) {
        if (method == null) {
            return false;
        }
        return mapper.insert(method) > 0;
    }

    @Override
    public boolean saveBatch(List<OzonDeliveryMethod> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        return mapper.insertBatch(list) > 0;
    }

    @Override
    public boolean update(OzonDeliveryMethod method) {
        if (method == null) {
            return false;
        }
        return mapper.updateById(method) > 0;
    }

    @Override
    public boolean removeByWarehouseId(Long warehouseId) {
        if (warehouseId == null) {
            return false;
        }
        return mapper.deleteByWarehouseId(warehouseId) >= 0;
    }

    @Override
    public boolean removeByWarehouseIds(List<Long> warehouseIds) {
        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return false;
        }
        return mapper.deleteByWarehouseIds(warehouseIds) >= 0;
    }
}