package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.CustomerSupport;

import java.util.List;

/**
 * 客户支持服务接口
 */
public interface CustomerSupportService {
    /**
     * 插入客户支持记录
     */
    int insert(CustomerSupport customerSupport);

    /**
     * 根据ID删除客户支持记录
     */
    int deleteById(Long supportId);

    /**
     * 更新客户支持记录
     */
    int update(CustomerSupport customerSupport);

    /**
     * 根据ID查询客户支持记录
     */
    CustomerSupport selectById(Long supportId);

    /**
     * 查询所有客户支持记录
     */
    List<CustomerSupport> selectAll();

    /**
     * 根据客户ID查询客户支持记录
     */
    List<CustomerSupport> selectByCustomerId(Long customerId);
}
