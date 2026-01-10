package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductStockSummaryMapper;
import com.example.shopeeerp.pojo.OzonProductStockSummary;
import com.example.shopeeerp.service.OzonProductStockSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Ozon商品库存汇总Service实现类
 */
@Service
public class OzonProductStockSummaryServiceImpl implements OzonProductStockSummaryService {

    @Autowired
    private OzonProductStockSummaryMapper ozonProductStockSummaryMapper;

    @Override
    public OzonProductStockSummary getById(Long id) {
        return ozonProductStockSummaryMapper.selectById(id);
    }

    @Override
    public OzonProductStockSummary getByProductId(Long productId) {
        return ozonProductStockSummaryMapper.selectByProductId(productId);
    }

    @Override
    public List<OzonProductStockSummary> getAll() {
        return ozonProductStockSummaryMapper.selectAll();
    }

    @Override
    public List<OzonProductStockSummary> getByCondition(OzonProductStockSummary condition) {
        return ozonProductStockSummaryMapper.selectByCondition(condition);
    }

    @Override
    public List<OzonProductStockSummary> getHasStock() {
        return ozonProductStockSummaryMapper.selectHasStock();
    }

    @Override
    public List<OzonProductStockSummary> getNoStock() {
        return ozonProductStockSummaryMapper.selectNoStock();
    }

    @Override
    @Transactional
    public boolean save(OzonProductStockSummary summary) {
        if (summary.getCreatedAt() == null) {
            summary.setCreatedAt(new Date());
        }
        if (summary.getUpdatedAt() == null) {
            summary.setUpdatedAt(new Date());
        }
        return ozonProductStockSummaryMapper.insert(summary) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonProductStockSummary summary) {
        summary.setUpdatedAt(new Date());
        return ozonProductStockSummaryMapper.updateById(summary) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return ozonProductStockSummaryMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean removeByProductId(Long productId) {
        return ozonProductStockSummaryMapper.deleteByProductId(productId) > 0;
    }

    @Override
    @Transactional
    public boolean removeBatch(List<Long> ids) {
        return ozonProductStockSummaryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public long count() {
        return ozonProductStockSummaryMapper.count();
    }

    @Override
    public long countHasStock() {
        return ozonProductStockSummaryMapper.countHasStock();
    }
}