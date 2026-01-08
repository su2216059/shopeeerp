package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.ProductItemMapper;
import com.example.shopeeerp.pojo.ProductItem;
import com.example.shopeeerp.service.ProductItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ozon商品详情服务实现类
 */
@Service
public class ProductItemServiceImpl implements ProductItemService {

    @Autowired
    private ProductItemMapper productItemMapper;

    @Override
    public int insert(ProductItem productItem) {
        return productItemMapper.insert(productItem);
    }

    @Override
    public int deleteById(Long itemId) {
        return productItemMapper.deleteById(itemId);
    }

    @Override
    public int update(ProductItem productItem) {
        return productItemMapper.update(productItem);
    }

    @Override
    public ProductItem selectById(Long itemId) {
        return productItemMapper.selectById(itemId);
    }

    @Override
    public List<ProductItem> selectAll() {
        return productItemMapper.selectAll();
    }

    @Override
    public ProductItem selectByOzonId(Long ozonId) {
        return productItemMapper.selectByOzonId(ozonId);
    }

    @Override
    public ProductItem selectByOfferId(String offerId) {
        return productItemMapper.selectByOfferId(offerId);
    }

    @Override
    public ProductItem selectBySku(Long sku) {
        return productItemMapper.selectBySku(sku);
    }

    @Override
    public List<ProductItem> selectByStatus(String status) {
        return productItemMapper.selectByStatus(status);
    }

    @Override
    public List<ProductItem> selectByArchived(Boolean isArchived) {
        return productItemMapper.selectByArchived(isArchived);
    }
}
