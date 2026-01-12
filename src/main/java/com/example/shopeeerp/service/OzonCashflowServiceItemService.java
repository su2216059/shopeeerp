package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowServiceItem;

import java.util.List;

public interface OzonCashflowServiceItemService {
    List<OzonCashflowServiceItem> getByPeriodId(Long periodId);

    boolean saveBatch(List<OzonCashflowServiceItem> list);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
