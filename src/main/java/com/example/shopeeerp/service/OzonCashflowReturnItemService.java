package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowReturnItem;

import java.util.List;

public interface OzonCashflowReturnItemService {
    List<OzonCashflowReturnItem> getByPeriodId(Long periodId);

    boolean saveBatch(List<OzonCashflowReturnItem> list);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
