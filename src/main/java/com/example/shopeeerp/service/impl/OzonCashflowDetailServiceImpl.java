package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonCashflowDetailMapper;
import com.example.shopeeerp.pojo.OzonCashflowDetail;
import com.example.shopeeerp.service.OzonCashflowDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OzonCashflowDetailServiceImpl implements OzonCashflowDetailService {

    @Autowired
    private OzonCashflowDetailMapper mapper;

    @Override
    public OzonCashflowDetail getByPeriodId(Long periodId) {
        return mapper.selectByPeriodId(periodId);
    }

    @Override
    @Transactional
    public boolean save(OzonCashflowDetail detail) {
        if (detail.getCreatedAt() == null) {
            detail.setCreatedAt(LocalDateTime.now());
        }
        return mapper.insert(detail) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonCashflowDetail detail) {
        return mapper.update(detail) > 0;
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
