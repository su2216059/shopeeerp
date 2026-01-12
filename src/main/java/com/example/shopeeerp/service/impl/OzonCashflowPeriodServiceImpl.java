package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonCashflowPeriodMapper;
import com.example.shopeeerp.pojo.OzonCashflowPeriod;
import com.example.shopeeerp.service.OzonCashflowPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonCashflowPeriodServiceImpl implements OzonCashflowPeriodService {

    @Autowired
    private OzonCashflowPeriodMapper periodMapper;

    @Override
    public List<OzonCashflowPeriod> getAll() {
        return periodMapper.selectAll();
    }

    @Override
    public OzonCashflowPeriod getById(Long id) {
        return periodMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean save(OzonCashflowPeriod period) {
        fillTimestamps(period);
        return periodMapper.insert(period) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonCashflowPeriod period) {
        if (period.getUpdatedAt() == null) {
            period.setUpdatedAt(LocalDateTime.now());
        }
        return periodMapper.update(period) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return periodMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        periodMapper.deleteAll();
        return true;
    }

    private void fillTimestamps(OzonCashflowPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        if (period.getCreatedAt() == null) {
            period.setCreatedAt(now);
        }
        if (period.getUpdatedAt() == null) {
            period.setUpdatedAt(now);
        }
    }
}
