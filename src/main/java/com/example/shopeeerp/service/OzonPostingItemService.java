package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonPostingItem;

import java.util.List;

public interface OzonPostingItemService {

    List<OzonPostingItem> getByPostingNumber(String postingNumber);

    boolean saveBatch(List<OzonPostingItem> items);

    boolean deleteByPostingNumber(String postingNumber);

    boolean deleteAll();
}
