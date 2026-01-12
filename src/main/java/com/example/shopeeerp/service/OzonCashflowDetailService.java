package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowDetail;

public interface OzonCashflowDetailService {
    OzonCashflowDetail getByPeriodId(Long periodId);

    boolean save(OzonCashflowDetail detail);

    boolean update(OzonCashflowDetail detail);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
