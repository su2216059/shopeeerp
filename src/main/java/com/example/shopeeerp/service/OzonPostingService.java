package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonPosting;

import java.math.BigDecimal;
import java.util.List;

public interface OzonPostingService {

    List<OzonPosting> getAll();

    List<OzonPosting> getAllByShopId(Long shopId);

    OzonPosting getByPostingNumber(String postingNumber);

    OzonPosting getByPostingNumberAndShopId(String postingNumber, Long shopId);

    boolean save(OzonPosting posting);

    boolean saveBatch(List<OzonPosting> postings);

    boolean update(OzonPosting posting);

    boolean updatePurchaseAmount(String postingNumber, BigDecimal purchaseAmount);

    boolean updatePurchaseAmount(String postingNumber, Long shopId, BigDecimal purchaseAmount);

    boolean deleteByPostingNumber(String postingNumber);

    boolean deleteAll();
}
