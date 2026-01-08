package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.Customer;

import java.util.List;

/**
 * 客户服务接口
 */
public interface CustomerService {
    /**
     * 插入客户记录
     */
    int insert(Customer customer);

    /**
     * 根据ID删除客户记录
     */
    int deleteById(Long customerId);

    /**
     * 更新客户记录
     */
    int update(Customer customer);

    /**
     * 根据ID查询客户记录
     */
    Customer selectById(Long customerId);

    /**
     * 查询所有客户记录
     */
    List<Customer> selectAll();

    /**
     * 根据邮箱查询客户记录
     */
    Customer selectByEmail(String email);
}
