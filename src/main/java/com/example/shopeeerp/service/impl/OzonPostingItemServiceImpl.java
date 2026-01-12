package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonPostingItemMapper;
import com.example.shopeeerp.pojo.OzonPostingItem;
import com.example.shopeeerp.service.OzonPostingItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonPostingItemServiceImpl implements OzonPostingItemService {

    @Autowired
    private OzonPostingItemMapper ozonPostingItemMapper;

    @Override
    public List<OzonPostingItem> getByPostingNumber(String postingNumber) {
        return ozonPostingItemMapper.selectByPostingNumber(postingNumber);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonPostingItem> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }
        items.forEach(this::fillTimestamps);
        return ozonPostingItemMapper.insertBatch(items) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByPostingNumber(String postingNumber) {
        ozonPostingItemMapper.deleteByPostingNumber(postingNumber);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        ozonPostingItemMapper.deleteAll();
        return true;
    }

    private void fillTimestamps(OzonPostingItem item) {
        LocalDateTime now = LocalDateTime.now();
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(now);
        }
        if (item.getUpdatedAt() == null) {
            item.setUpdatedAt(now);
        }
    }
}
