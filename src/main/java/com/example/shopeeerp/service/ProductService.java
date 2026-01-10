package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProduct;

import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    int insert(OzonProduct product);
    int deleteById(Long productId);
    int update(OzonProduct product);
    OzonProduct selectById(Long productId);
    List<OzonProduct> selectAll();
    OzonProduct selectBySku(Long sku);
}
