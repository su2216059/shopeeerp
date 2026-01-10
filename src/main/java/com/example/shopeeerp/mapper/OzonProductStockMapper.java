package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonProductStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ozon商品库存明细Mapper接口
 */
@Mapper
public interface OzonProductStockMapper {

    /**
     * 根据ID查询库存明细
     */
    OzonProductStock selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询库存明细列表
     */
    List<OzonProductStock> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据SKU查询库存明细
     */
    List<OzonProductStock> selectBySku(@Param("sku") Long sku);

    /**
     * 根据来源查询库存明细
     */
    List<OzonProductStock> selectBySource(@Param("source") String source);

    /**
     * 根据商品ID和来源查询库存明细
     */
    OzonProductStock selectByProductIdAndSource(@Param("productId") Long productId, @Param("source") String source);

    /**
     * 查询所有库存明细
     */
    List<OzonProductStock> selectAll();

    /**
     * 根据条件查询库存明细列表
     */
    List<OzonProductStock> selectByCondition(OzonProductStock condition);

    /**
     * 插入库存明细
     */
    int insert(OzonProductStock stock);

    /**
     * 批量插入库存明细
     */
    int insertBatch(@Param("list") List<OzonProductStock> stocks);

    /**
     * 根据ID更新库存明细
     */
    int updateById(OzonProductStock stock);

    /**
     * 根据商品ID和来源更新库存明细
     */
    int updateByProductIdAndSource(OzonProductStock stock);

    /**
     * 根据ID删除库存明细
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据商品ID删除库存明细
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 根据SKU删除库存明细
     */
    int deleteBySku(@Param("sku") Long sku);

    /**
     * 根据商品ID和来源删除库存明细
     */
    int deleteByProductIdAndSource(@Param("productId") Long productId, @Param("source") String source);

    /**
     * 批量删除库存明细
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 统计库存明细数量
     */
    long count();

    /**
     * 根据商品ID统计库存明细数量
     */
    long countByProductId(@Param("productId") Long productId);

    /**
     * 计算商品总可用库存
     */
    int sumPresentByProductId(@Param("productId") Long productId);

    /**
     * 计算商品总预留库存
     */
    int sumReservedByProductId(@Param("productId") Long productId);
}