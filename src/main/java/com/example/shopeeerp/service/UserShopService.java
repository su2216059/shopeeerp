package com.example.shopeeerp.service;

import java.util.List;

public interface UserShopService {
    boolean hasShopAccess(Long userId, Long shopId);

    List<Long> getShopIdsByUserId(Long userId);

    void addUserShop(Long userId, Long shopId, String role);
}
