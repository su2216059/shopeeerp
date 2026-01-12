package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonCashflowSummaryMapper;
import com.example.shopeeerp.pojo.OzonCashflowSummary;
import com.example.shopeeerp.service.OzonCashflowSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonCashflowSummaryServiceImpl implements OzonCashflowSummaryService {

    @Autowired
    private OzonCashflowSummaryMapper mapper;

    @Override
    public List<OzonCashflowSummary> getByPeriodId(Long periodId) {
        return mapper.selectByPeriodId(periodId);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonCashflowSummary> list) {
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
