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
     * 插入商品记录
     */
    int insert(OzonProduct product);

    /**
     * 批量插入商品记录
     */
    int batchInsert(@Param("list") List<OzonProduct> products);

    /**
     * 根据ID删除商品记录
     */
    int deleteById(Long id);

    /**
     * 更新商品记录
     */
    int update(OzonProduct product);

    /**
     * 插入或更新商品记录
     */
    int insertOrUpdate(OzonProduct product);

    /**
     * 根据ID查询商品记录
     */
    OzonProduct selectById(Long id);

    /**
     * 根据offerId查询商品记录
     */
    OzonProduct selectByOfferId(@Param("offerId") String offerId);

    /**
     * 根据SKU查询商品记录
     */
    OzonProduct selectBySku(@Param("sku") Long sku);

    /**
     * 查询所有商品记录
     */
    List<OzonProduct> selectAll();

    /**
     * 根据归档状态查询商品
     */
    List<OzonProduct> selectByArchived(@Param("isArchived") Boolean isArchived);

    /**
     * 根据ID查询商品及其关联信息
     */
    OzonProduct selectWithDetailsById(Long id);

    /**
     * 查询所有商品及其关联信息
     */
    List<OzonProduct> selectAllWithDetails();
}
