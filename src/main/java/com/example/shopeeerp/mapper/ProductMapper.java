package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper {
    /**
     * 插入产品记录
     */
    int insert(Product product);

    /**
     * 根据ID删除产品记录
     */
    int deleteById(Long productId);

    /**
     * 更新产品记录
     */
    int update(Product product);

    /**
     * 根据ID查询产品记录
     */
    Product selectById(Long productId);

    /**
     * 查询所有产品记录
     */
    List<Product> selectAll();

    /**
     * 根据SKU查询产品记录
     */
    Product selectBySku(@Param("sku") String sku);

    /**
     * 根据分类ID查询产品记录
     */
    List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);
}
