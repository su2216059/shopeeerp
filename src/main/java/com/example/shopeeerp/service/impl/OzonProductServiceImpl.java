package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductMapper;
import com.example.shopeeerp.pojo.OzonProduct;
import com.example.shopeeerp.service.cache.PurchasePriceCacheService;
import com.example.shopeeerp.service.OzonProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Ozon鍟嗗搧Service瀹炵幇绫?
 */
@Service
public class OzonProductServiceImpl implements OzonProductService {

    @Autowired
    private OzonProductMapper ozonProductMapper;

    @Autowired
    private PurchasePriceCacheService purchasePriceCacheService;

    @Override
    public OzonProduct getById(Long id, Long shopId) {
        return ozonProductMapper.selectById(id, shopId);
    }

    @Override
    public OzonProduct getByOfferId(String offerId, Long shopId) {
        return ozonProductMapper.selectByOfferId(offerId, shopId);
    }

    @Override
    public OzonProduct getBySku(Long sku, Long shopId) {
        return ozonProductMapper.selectBySku(sku, shopId);
    }

    @Override
    public List<OzonProduct> getAll(Long shopId) {
        return ozonProductMapper.selectAll(shopId);
    }

    @Override
    public List<OzonProduct> getByCondition(OzonProduct condition) {
        return ozonProductMapper.selectByCondition(condition);
    }

    @Override
    public List<OzonProduct> getByFilters(String title,
                                          Long shopId,
                                          String productCode,
                                          LocalDateTime createdFrom,
                                          LocalDateTime createdTo,
                                          String visibility) {
        return ozonProductMapper.selectByFilters(title, shopId, productCode, createdFrom, createdTo, visibility);
    }

    @Override
    public List<OzonProduct> getByPage(int pageNum, int pageSize, Long shopId) {
        return getAll(shopId);
    }

    @Override
    @Transactional
    public boolean save(OzonProduct product) {
        if (product.getSyncTime() == null) {
            product.setSyncTime(LocalDateTime.now());
        }
        boolean saved = ozonProductMapper.insert(product) > 0;
        if (saved && product.getShopId() != null && product.getSku() != null) {
            purchasePriceCacheService.evictPurchasePrice(product.getShopId(), product.getSku());
        }
        return saved;
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
        boolean saved = ozonProductMapper.insertBatch(products) > 0;
        if (saved && products != null && !products.isEmpty()) {
            Long shopId = products.get(0).getShopId();
            List<Long> skus = products.stream()
                    .map(OzonProduct::getSku)
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toList());
            if (shopId != null && !skus.isEmpty()) {
                purchasePriceCacheService.evictPurchasePriceBatch(shopId, skus);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean update(OzonProduct product) {
        boolean updated = ozonProductMapper.updateById(product) > 0;
        if (updated && product.getShopId() != null && product.getSku() != null) {
            purchasePriceCacheService.evictPurchasePrice(product.getShopId(), product.getSku());
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean removeById(Long id, Long shopId) {
        return ozonProductMapper.deleteById(id, shopId) > 0;
    }

    @Override
    @Transactional
    public boolean removeBatch(List<Long> ids, Long shopId) {
        return ozonProductMapper.deleteBatch(ids, shopId) > 0;
    }

    @Override
    public long count(Long shopId) {
        return ozonProductMapper.count(shopId);
    }

    @Override
    public long countByCondition(OzonProduct condition) {
        return ozonProductMapper.countByCondition(condition);
    }

    @Override
    public BigDecimal getPurchasePrice(Long shopId, String sku) {
        OzonProduct ozonProduct = ozonProductMapper.selectBySku(Long.valueOf(sku), shopId);
        return ozonProduct.getPurchasePrice();
    }
}


