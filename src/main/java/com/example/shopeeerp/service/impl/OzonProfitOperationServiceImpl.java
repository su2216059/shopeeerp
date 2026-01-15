package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProfitOperationMapper;
import com.example.shopeeerp.pojo.OzonProfitOperation;
import com.example.shopeeerp.service.OzonProfitOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OzonProfitOperationServiceImpl implements OzonProfitOperationService {

    @Autowired
    private OzonProfitOperationMapper mapper;

    @Override
    public OzonProfitOperation getByOperationId(Long operationId) {
        return mapper.selectByOperationId(operationId);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonProfitOperation> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        list.forEach(op -> {
            if (op.getCreatedAt() == null) {
                op.setCreatedAt(now);
            }
            op.setUpdatedAt(now);
            if (op.getPurchaseCost() == null) {
                op.setPurchaseCost(java.math.BigDecimal.ZERO);
            }
        });
        return mapper.insertBatch(list) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByDateRange(LocalDateTime from, LocalDateTime to) {
        return mapper.deleteByDateRange(from, to) >= 0;
    }

    @Override
    @Transactional
    public boolean deleteByPostingNumber(String postingNumber) {
        return mapper.deleteByPostingNumber(postingNumber) >= 0;
    }
}
