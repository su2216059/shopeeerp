package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductStockMapper;
import com.example.shopeeerp.pojo.OzonProductStock;
import com.example.shopeeerp.service.OzonProductStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Ozon商品库存明细Service实现类
 */
@Service
public class OzonProductStockServiceImpl implements OzonProductStockService {

    @Autowired
    private OzonProductStockMapper ozonProductStockMapper;

    @Override
    public OzonProductStock getById(Long id) {
        return ozonProductStockMapper.selectById(id);
    }

    @Override
    public List<OzonProductStock> getByProductId(Long productId) {
        return ozonProductStockMapper.selectByProductId(productId);
    }

    @Override
    public List<OzonProductStock> getBySku(Long sku) {
        return ozonProductStockMapper.selectBySku(sku);
    }

    @Override
    public List<OzonProductStock> getBySource(String source) {
        return ozonProductStockMapper.selectBySource(source);
    }

    @Override
    public OzonProductStock getByProductIdAndSource(Long productId, String source) {
        return ozonProductStockMapper.selectByProductIdAndSource(productId, source);
    }

    @Override
    public List<OzonProductStock> getAll() {
        return ozonProductStockMapper.selectAll();
    }

    @Override
    public List<OzonProductStock> getByCondition(OzonProductStock condition) {
        return ozonProductStockMapper.selectByCondition(condition);
    }

    @Override
    @Transactional
    public boolean save(OzonProductStock stock) {
        if (stock.getCreatedAt() == null) {
            stock.setCreatedAt(new Date());
        }
        if (stock.getUpdatedAt() == null) {
            stock.setUpdatedAt(new Date());
        }
        return ozonProductStockMapper.insert(stock) > 0;
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonProductStock> stocks) {
        Date now = new Date();
        stocks.forEach(stock -> {
            if (stock.getCreatedAt() == null) {
                stock.setCreatedAt(now);
            }
            if (stock.getUpdatedAt() == null) {
                stock.setUpdatedAt(now);
            }
        });
        return ozonProductStockMapper.insertBatch(stocks) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonProductStock stock) {
        stock.setUpdatedAt(new Date());
        return ozonProductStockMapper.updateById(stock) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return ozonProductStockMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean removeByProductId(Long productId) {
        return ozonProductStockMapper.deleteByProductId(productId) > 0;
    }

    @Override
    @Transactional
    public boolean removeBySku(Long sku) {
        return ozonProductStockMapper.deleteBySku(sku) > 0;
    }

    @Override
    @Transactional
    public boolean removeByProductIdAndSource(Long productId, String source) {
        return ozonProductStockMapper.deleteByProductIdAndSource(productId, source) > 0;
    }

    @Override
    @Transactional
    public boolean removeBatch(List<Long> ids) {
        return ozonProductStockMapper.deleteBatch(ids) > 0;
    }

    @Override
    public long count() {
        return ozonProductStockMapper.count();
    }

    @Override
    public long countByProductId(Long productId) {
        return ozonProductStockMapper.countByProductId(productId);
    }

    @Override
    public int sumPresentByProductId(Long productId) {
        return ozonProductStockMapper.sumPresentByProductId(productId);
    }

    @Override
    public int sumReservedByProductId(Long productId) {
        return ozonProductStockMapper.sumReservedByProductId(productId);
    }
}