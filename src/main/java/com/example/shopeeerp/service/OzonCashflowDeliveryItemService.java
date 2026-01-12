package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonCashflowDeliveryItem;

import java.util.List;

public interface OzonCashflowDeliveryItemService {
    List<OzonCashflowDeliveryItem> getByPeriodId(Long periodId);

    boolean saveBatch(List<OzonCashflowDeliveryItem> list);

    boolean deleteByPeriodId(Long periodId);

    boolean deleteAll();
}
