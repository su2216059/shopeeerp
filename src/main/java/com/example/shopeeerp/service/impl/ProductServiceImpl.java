package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.OzonProductMapper;
import com.example.shopeeerp.pojo.OzonProduct;
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
    private OzonProductMapper productMapper;

    @Override
    public int insert(OzonProduct product) {
        return productMapper.insert(product);
    }

    @Override
    public int deleteById(Long productId) {
        return productMapper.deleteById(productId);
    }

    @Override
    public int update(OzonProduct product) {
        return productMapper.updateById(product);
    }

    @Override
    public OzonProduct selectById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public List<OzonProduct> selectAll() {
        return productMapper.selectAll();
    }

    @Override
    public OzonProduct selectBySku(Long sku) {
        return productMapper.selectBySku(sku);
    }


}
