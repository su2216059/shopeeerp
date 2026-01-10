package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ozon商品Mapper接口
 */
@Mapper
public interface OzonProductMapper {

    /**
     * 根据ID查询商品
     */
    OzonProduct selectById(@Param("id") Long id);

    /**
     * 根据offerId查询商品
     */
    OzonProduct selectByOfferId(@Param("offerId") String offerId);

    /**
     * 根据SKU查询商品
     */
    OzonProduct selectBySku(@Param("sku") Long sku);

    /**
     * 查询所有商品
     */
    List<OzonProduct> selectAll();

    /**
     * 根据条件查询商品列表
     */
    List<OzonProduct> selectByCondition(OzonProduct condition);

    /**
     * 插入商品
     */
    int insert(OzonProduct product);

    /**
     * 批量插入商品
     */
    int insertBatch(@Param("list") List<OzonProduct> products);

    /**
     * 根据ID更新商品
     */
    int updateById(OzonProduct product);

    /**
     * 根据ID删除商品
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除商品
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 统计商品数量
     */
    long count();

    /**
     * 根据条件统计商品数量
     */
    long countByCondition(OzonProduct condition);
}