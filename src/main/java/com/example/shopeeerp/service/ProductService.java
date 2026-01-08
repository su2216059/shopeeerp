package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Product;

import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    int insert(Product product);
    int deleteById(Long productId);
    int update(Product product);
    Product selectById(Long productId);
    List<Product> selectAll();
    Product selectBySku(String sku);
    List<Product> selectByCategoryId(Long categoryId);
}
