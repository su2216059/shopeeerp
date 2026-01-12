package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowPayment;

import java.util.List;

public interface OzonCashflowPaymentService {
    List<OzonCashflowPayment> getByPeriodId(Long periodId);

    boolean saveBatch(List<OzonCashflowPayment> list);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
