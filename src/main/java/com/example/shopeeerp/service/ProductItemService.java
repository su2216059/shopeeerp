package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.ProductItem;

import java.util.List;

/**
 * Ozon商品详情服务接口
 */
public interface ProductItemService {
    int insert(ProductItem productItem);
    int deleteById(Long itemId);
    int update(ProductItem productItem);
    ProductItem selectById(Long itemId);
    List<ProductItem> selectAll();
    ProductItem selectByOzonId(Long ozonId);
    ProductItem selectByOfferId(String offerId);
    ProductItem selectBySku(Long sku);
    List<ProductItem> selectByStatus(String status);
    List<ProductItem> selectByArchived(Boolean isArchived);
}
