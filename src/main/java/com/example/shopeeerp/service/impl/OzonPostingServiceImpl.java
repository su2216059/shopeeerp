package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonPostingMapper;
import com.example.shopeeerp.pojo.OzonPosting;
import com.example.shopeeerp.service.OzonPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonPostingServiceImpl implements OzonPostingService {

    @Autowired
    private OzonPostingMapper ozonPostingMapper;

    @Override
    public List<OzonPosting> getAll() {
        return ozonPostingMapper.selectAll();
    }

    @Override
    public OzonPosting getByPostingNumber(String postingNumber) {
        return ozonPostingMapper.selectByPostingNumber(postingNumber);
    }

    @Override
    @Transactional
    public boolean save(OzonPosting posting) {
        fillTimestamps(posting);
        return ozonPostingMapper.insert(posting) > 0;
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonPosting> postings) {
        if (postings == null || postings.isEmpty()) {
            return true;
        }
        postings.forEach(this::fillTimestamps);
        return ozonPostingMapper.insertBatch(postings) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonPosting posting) {
        if (posting.getUpdatedAt() == null) {
            posting.setUpdatedAt(LocalDateTime.now());
        }
        return ozonPostingMapper.update(posting) > 0;
    }

    @Override
    @Transactional
    public boolean updatePurchaseAmount(String postingNumber, BigDecimal purchaseAmount) {
        if (postingNumber == null || postingNumber.trim().isEmpty()) {
            return false;
        }
        BigDecimal normalized = purchaseAmount != null ? purchaseAmount : BigDecimal.ZERO;
        return ozonPostingMapper.updatePurchaseAmount(postingNumber, normalized, LocalDateTime.now()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByPostingNumber(String postingNumber) {
        return ozonPostingMapper.deleteByPostingNumber(postingNumber) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAll() {
        ozonPostingMapper.deleteAll();
        return true;
    }

    private void fillTimestamps(OzonPosting posting) {
        LocalDateTime now = LocalDateTime.now();
        if (posting.getCreatedAt() == null) {
            posting.setCreatedAt(now);
        }
        if (posting.getUpdatedAt() == null) {
            posting.setUpdatedAt(now);
        }
        if (posting.getPurchaseAmount() == null) {
            posting.setPurchaseAmount(BigDecimal.ZERO);
        }
    }
}
