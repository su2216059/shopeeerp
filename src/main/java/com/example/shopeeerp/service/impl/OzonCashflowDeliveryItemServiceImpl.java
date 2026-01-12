package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonCashflowDeliveryItemMapper;
import com.example.shopeeerp.pojo.OzonCashflowDeliveryItem;
import com.example.shopeeerp.service.OzonCashflowDeliveryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonCashflowDeliveryItemServiceImpl implements OzonCashflowDeliveryItemService {

    @Autowired
    private OzonCashflowDeliveryItemMapper mapper;

    @Override
    public List<OzonCashflowDeliveryItem> getByPeriodId(Long periodId) {
        return mapper.selectByPeriodId(periodId);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonCashflowDeliveryItem> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        list.forEach(item -> {
            if (item.getCreatedAt() == null) {
                item.setCreatedAt(now);
            }
        });
        return mapper.insertBatch(list) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByPeriodId(Long periodId) {
        mapper.deleteByPeriodId(periodId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        mapper.deleteAll();
        return true;
    }
}
