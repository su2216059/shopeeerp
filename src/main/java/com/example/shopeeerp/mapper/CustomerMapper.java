package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
// 提交测试
/**
 * 客户Mapper接口
 */
@Mapper
public interface CustomerMapper {
    /**
     * 插入客户记录
     */
    int insert(Customer customer);

    /**
     * 根据ID删除客户记录
     */
    int deleteById(@Param("customerId") Long customerId, @Param("shopId") Long shopId);

    /**
     * 更新客户记录
     */
    int update(Customer customer);

    /**
     * 根据ID查询客户记录
     */
    Customer selectById(@Param("customerId") Long customerId, @Param("shopId") Long shopId);

    /**
     * 查询所有客户记录
     */
    List<Customer> selectAll(@Param("shopId") Long shopId);

    /**
     * 根据邮箱查询客户记录
     */
    Customer selectByEmail(@Param("email") String email, @Param("shopId") Long shopId);
}
