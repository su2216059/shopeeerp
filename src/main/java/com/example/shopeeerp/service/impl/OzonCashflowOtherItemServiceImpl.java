package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonCashflowOtherItemMapper;
import com.example.shopeeerp.pojo.OzonCashflowOtherItem;
import com.example.shopeeerp.service.OzonCashflowOtherItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonCashflowOtherItemServiceImpl implements OzonCashflowOtherItemService {

    @Autowired
    private OzonCashflowOtherItemMapper mapper;

    @Override
    public List<OzonCashflowOtherItem> getByPeriodId(Long periodId) {
        return mapper.selectByPeriodId(periodId);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonCashflowOtherItem> list) {
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
