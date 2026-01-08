package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.ProductItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ozon商品详情Mapper接口
 */
@Mapper
public interface ProductItemMapper {
    /**
     * 插入商品详情记录
     */
    int insert(ProductItem productItem);

    /**
     * 根据ID删除商品详情记录
     */
    int deleteById(Long itemId);

    /**
     * 更新商品详情记录
     */
    int update(ProductItem productItem);

    /**
     * 根据ID查询商品详情记录
     */
    ProductItem selectById(Long itemId);

    /**
     * 查询所有商品详情记录
     */
    List<ProductItem> selectAll();

    /**
     * 根据Ozon ID查询商品详情记录
     */
    ProductItem selectByOzonId(@Param("ozonId") Long ozonId);

    /**
     * 根据Offer ID查询商品详情记录
     */
    ProductItem selectByOfferId(@Param("offerId") String offerId);

    /**
     * 根据SKU查询商品详情记录
     */
    ProductItem selectBySku(@Param("sku") Long sku);

    /**
     * 根据状态查询商品详情记录列表
     */
    List<ProductItem> selectByStatus(@Param("status") String status);

    /**
     * 根据归档状态查询商品详情记录列表
     */
    List<ProductItem> selectByArchived(@Param("isArchived") Boolean isArchived);
}
