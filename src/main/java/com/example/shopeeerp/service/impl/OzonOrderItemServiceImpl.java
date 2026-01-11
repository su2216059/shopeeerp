package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonOrderItemMapper;
import com.example.shopeeerp.pojo.OzonOrderItem;
import com.example.shopeeerp.service.OzonOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonOrderItemServiceImpl implements OzonOrderItemService {

    @Autowired
    private OzonOrderItemMapper ozonOrderItemMapper;

    @Override
    public List<OzonOrderItem> getByOrderId(Long orderId) {
        return ozonOrderItemMapper.selectByOrderId(orderId);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonOrderItem> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }
        items.forEach(this::fillTimestamps);
        return ozonOrderItemMapper.insertBatch(items) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByOrderId(Long orderId) {
        ozonOrderItemMapper.deleteByOrderId(orderId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        ozonOrderItemMapper.deleteAll();
        return true;
    }

    @Override
    @Transactional
    public boolean replaceItems(Long orderId, List<OzonOrderItem> items) {
        deleteByOrderId(orderId);
        return saveBatch(items);
    }

    private void fillTimestamps(OzonOrderItem item) {
        LocalDateTime now = LocalDateTime.now();
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(now);
        }
        if (item.getUpdatedAt() == null) {
            item.setUpdatedAt(now);
        }
    }
}
