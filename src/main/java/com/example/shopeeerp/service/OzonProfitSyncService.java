package com.example.shopeeerp.service;

public interface OzonProfitSyncService {

    /**
     * 根据 posting_number 拉取 Ozon 财务交易并入库
     */
    void sync(String postingNumber, String from, String to);
}
