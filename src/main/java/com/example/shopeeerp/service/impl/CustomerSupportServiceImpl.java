package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.CustomerSupportMapper;
import com.example.shopeeerp.pojo.CustomerSupport;
import com.example.shopeeerp.service.CustomerSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户支持服务实现类
 */
@Service
public class CustomerSupportServiceImpl implements CustomerSupportService {

    @Autowired
    private CustomerSupportMapper customerSupportMapper;

    @Override
    public int insert(CustomerSupport customerSupport) {
        return customerSupportMapper.insert(customerSupport);
    }

    @Override
    public int deleteById(Long supportId, Long shopId) {
        return customerSupportMapper.deleteById(supportId, shopId);
    }

    @Override
    public int update(CustomerSupport customerSupport) {
        return customerSupportMapper.update(customerSupport);
    }

    @Override
    public CustomerSupport selectById(Long supportId, Long shopId) {
        return customerSupportMapper.selectById(supportId, shopId);
    }

    @Override
    public List<CustomerSupport> selectAll(Long shopId) {
        return customerSupportMapper.selectAll(shopId);
    }

    @Override
    public List<CustomerSupport> selectByCustomerId(Long customerId, Long shopId) {
        return customerSupportMapper.selectByCustomerId(customerId, shopId);
    }
}
