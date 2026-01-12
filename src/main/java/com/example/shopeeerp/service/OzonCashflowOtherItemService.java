package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowOtherItem;

import java.util.List;

public interface OzonCashflowOtherItemService {
    List<OzonCashflowOtherItem> getByPeriodId(Long periodId);

    boolean saveBatch(List<OzonCashflowOtherItem> list);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
