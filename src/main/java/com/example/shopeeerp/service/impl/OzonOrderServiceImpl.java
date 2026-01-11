package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonOrderMapper;
import com.example.shopeeerp.pojo.OzonOrder;
import com.example.shopeeerp.service.OzonOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonOrderServiceImpl implements OzonOrderService {

    @Autowired
    private OzonOrderMapper ozonOrderMapper;

    @Override
    public List<OzonOrder> getAll() {
        return ozonOrderMapper.selectAll();
    }

    @Override
    public OzonOrder getById(Long id) {
        return ozonOrderMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean save(OzonOrder order) {
        fillTimestamps(order);
        return ozonOrderMapper.insert(order) > 0;
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return true;
        }
        orders.forEach(this::fillTimestamps);
        for (OzonOrder order : orders) {
            ozonOrderMapper.insert(order);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean update(OzonOrder order) {
        if (order.getUpdatedAt() == null) {
            order.setUpdatedAt(LocalDateTime.now());
        }
        return ozonOrderMapper.update(order) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return ozonOrderMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean removeAll() {
        ozonOrderMapper.deleteAll();
        return true;
    }

    @Override
    @Transactional
    public boolean replaceAll(List<OzonOrder> orders) {
        removeAll();
        return saveBatch(orders);
    }

    private void fillTimestamps(OzonOrder order) {
        LocalDateTime now = LocalDateTime.now();
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(now);
        }
        if (order.getUpdatedAt() == null) {
            order.setUpdatedAt(now);
        }
        if (order.getSyncTime() == null) {
            order.setSyncTime(now);
        }
    }
}
