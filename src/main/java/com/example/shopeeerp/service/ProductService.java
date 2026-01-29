package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProduct;

import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    int insert(OzonProduct product);
    int deleteById(Long productId, Long shopId);
    int update(OzonProduct product);
    OzonProduct selectById(Long productId, Long shopId);
    List<OzonProduct> selectAll(Long shopId);
    OzonProduct selectBySku(Long sku, Long shopId);
}
