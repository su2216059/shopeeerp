package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.ProductMapper;
import com.example.shopeeerp.pojo.Product;
import com.example.shopeeerp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public int insert(Product product) {
        return productMapper.insert(product);
    }

    @Override
    public int deleteById(Long productId) {
        return productMapper.deleteById(productId);
    }

    @Override
    public int update(Product product) {
        return productMapper.update(product);
    }

    @Override
    public Product selectById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public List<Product> selectAll() {
        return productMapper.selectAll();
    }

    @Override
    public Product selectBySku(String sku) {
        return productMapper.selectBySku(sku);
    }

    @Override
    public List<Product> selectByCategoryId(Long categoryId) {
        return productMapper.selectByCategoryId(categoryId);
    }
}
