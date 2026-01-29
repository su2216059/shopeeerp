package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.UserShopMapper;
import com.example.shopeeerp.pojo.UserShop;
import com.example.shopeeerp.service.UserShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserShopServiceImpl implements UserShopService {

    @Autowired
    private UserShopMapper userShopMapper;

    @Override
    public boolean hasShopAccess(Long userId, Long shopId) {
        if (userId == null || shopId == null) {
            return false;
        }
        return userShopMapper.countByUserIdAndShopId(userId, shopId) > 0;
    }

    @Override
    public List<Long> getShopIdsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return userShopMapper.selectShopIdsByUserId(userId);
    }

    @Override
    public void addUserShop(Long userId, Long shopId, String role) {
        if (userId == null || shopId == null) {
            return;
        }
        UserShop userShop = new UserShop();
        userShop.setUserId(userId);
        userShop.setShopId(shopId);
        userShop.setRole(role == null ? "owner" : role);
        userShopMapper.insert(userShop);
    }
}
