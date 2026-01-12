package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowPeriod;

import java.util.List;

public interface OzonCashflowPeriodService {
    List<OzonCashflowPeriod> getAll();

    OzonCashflowPeriod getById(Long id);

    boolean save(OzonCashflowPeriod period);

    boolean update(OzonCashflowPeriod period);

    boolean deleteById(Long id);

    boolean deleteAll();
}
