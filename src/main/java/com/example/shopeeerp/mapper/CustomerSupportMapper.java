package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.CustomerSupport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户支持Mapper接口
 */
@Mapper
public interface CustomerSupportMapper {
    /**
     * 插入客户支持记录
     */
    int insert(CustomerSupport customerSupport);

    /**
     * 根据ID删除客户支持记录
     */
    int deleteById(@Param("supportId") Long supportId, @Param("shopId") Long shopId);

    /**
     * 更新客户支持记录
     */
    int update(CustomerSupport customerSupport);

    /**
     * 根据ID查询客户支持记录
     */
    CustomerSupport selectById(@Param("supportId") Long supportId, @Param("shopId") Long shopId);

    /**
     * 查询所有客户支持记录
     */
    List<CustomerSupport> selectAll(@Param("shopId") Long shopId);

    /**
     * 根据客户ID查询客户支持记录
     */
    List<CustomerSupport> selectByCustomerId(@Param("customerId") Long customerId, @Param("shopId") Long shopId);
}
