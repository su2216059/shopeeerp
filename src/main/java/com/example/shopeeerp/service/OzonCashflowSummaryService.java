package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowSummary;

import java.util.List;

public interface OzonCashflowSummaryService {
    List<OzonCashflowSummary> getByPeriodId(Long periodId);

    boolean saveBatch(List<OzonCashflowSummary> list);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
