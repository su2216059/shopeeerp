package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.SalesDataMapper;
import com.example.shopeeerp.pojo.SalesData;
import com.example.shopeeerp.service.SalesDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 销售数据服务实现类
 */
@Service
public class SalesDataServiceImpl implements SalesDataService {

    @Autowired
    private SalesDataMapper salesDataMapper;

    @Override
    public int insert(SalesData salesData) {
        return salesDataMapper.insert(salesData);
    }

    @Override
    public int deleteById(Long salesId) {
        return salesDataMapper.deleteById(salesId);
    }

    @Override
    public int update(SalesData salesData) {
        return salesDataMapper.update(salesData);
    }

    @Override
    public SalesData selectById(Long salesId) {
        return salesDataMapper.selectById(salesId);
    }

    @Override
    public List<SalesData> selectAll() {
        return salesDataMapper.selectAll();
    }

    @Override
    public List<SalesData> selectByProductId(Long productId) {
        return salesDataMapper.selectByProductId(productId);
    }

    @Override
    public List<SalesData> selectByOrderId(Long orderId) {
        return salesDataMapper.selectByOrderId(orderId);
    }
}
