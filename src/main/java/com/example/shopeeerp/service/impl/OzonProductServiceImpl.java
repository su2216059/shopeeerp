package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductMapper;
import com.example.shopeeerp.pojo.OzonProduct;
import com.example.shopeeerp.service.OzonProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Ozon商品Service实现类
 */
@Service
public class OzonProductServiceImpl implements OzonProductService {

    @Autowired
    private OzonProductMapper ozonProductMapper;

    @Override
    public OzonProduct getById(Long id) {
        return ozonProductMapper.selectById(id);
    }

    @Override
    public OzonProduct getByOfferId(String offerId) {
        return ozonProductMapper.selectByOfferId(offerId);
    }

    @Override
    public OzonProduct getBySku(Long sku) {
        return ozonProductMapper.selectBySku(sku);
    }

    @Override
    public List<OzonProduct> getAll() {
        return ozonProductMapper.selectAll();
    }

    @Override
    public List<OzonProduct> getByCondition(OzonProduct condition) {
        return ozonProductMapper.selectByCondition(condition);
    }

    @Override
    public List<OzonProduct> getByFilters(String title,
                                          String productCode,
                                          LocalDateTime createdFrom,
                                          LocalDateTime createdTo,
                                          String visibility) {
        return ozonProductMapper.selectByFilters(title, productCode, createdFrom, createdTo, visibility);
    }

    @Override
    public List<OzonProduct> getByPage(int pageNum, int pageSize) {
        // 这里可以实现分页逻辑，暂时返回所有数据
        return getAll();
    }

    @Override
    @Transactional
    public boolean save(OzonProduct product) {
        if (product.getSyncTime() == null) {
            product.setSyncTime(LocalDateTime.now());
        }
        return ozonProductMapper.insert(product) > 0;
    }

    @Override
    @Transactional
    public boolean saveBatch(List<OzonProduct> products) {
        LocalDateTime now = LocalDateTime.now();
        products.forEach(product -> {
            if (product.getSyncTime() == null) {
                product.setSyncTime(now);
            }
        });
        return ozonProductMapper.insertBatch(products) > 0;
    }

    @Override
    @Transactional
    public boolean update(OzonProduct product) {
        return ozonProductMapper.updateById(product) > 0;
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return ozonProductMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean removeBatch(List<Long> ids) {
        return ozonProductMapper.deleteBatch(ids) > 0;
    }

    @Override
    public long count() {
        return ozonProductMapper.count();
    }

    @Override
    public long countByCondition(OzonProduct condition) {
        return ozonProductMapper.countByCondition(condition);
    }
}
