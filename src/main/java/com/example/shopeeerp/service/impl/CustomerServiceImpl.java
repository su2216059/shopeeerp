package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.CustomerMapper;
import com.example.shopeeerp.pojo.Customer;
import com.example.shopeeerp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public int insert(Customer customer) {
        return customerMapper.insert(customer);
    }

    @Override
    public int deleteById(Long customerId, Long shopId) {
        return customerMapper.deleteById(customerId, shopId);
    }

    @Override
    public int update(Customer customer) {
        return customerMapper.update(customer);
    }

    @Override
    public Customer selectById(Long customerId, Long shopId) {
        return customerMapper.selectById(customerId, shopId);
    }

    @Override
    public List<Customer> selectAll(Long shopId) {
        return customerMapper.selectAll(shopId);
    }

    @Override
    public Customer selectByEmail(String email, Long shopId) {
        return customerMapper.selectByEmail(email, shopId);
    }
}
