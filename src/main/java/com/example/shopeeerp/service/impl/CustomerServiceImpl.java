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
    public int deleteById(Long customerId) {
        return customerMapper.deleteById(customerId);
    }

    @Override
    public int update(Customer customer) {
        return customerMapper.update(customer);
    }

    @Override
    public Customer selectById(Long customerId) {
        return customerMapper.selectById(customerId);
    }

    @Override
    public List<Customer> selectAll() {
        return customerMapper.selectAll();
    }

    @Override
    public Customer selectByEmail(String email) {
        return customerMapper.selectByEmail(email);
    }
}
