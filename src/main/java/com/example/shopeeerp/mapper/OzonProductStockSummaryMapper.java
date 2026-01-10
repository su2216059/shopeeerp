package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.OzonProductStockSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Ozon商品库存汇总Mapper接口
 */
@Mapper
public interface OzonProductStockSummaryMapper {

    /**
     * 根据ID查询库存汇总
     */
    OzonProductStockSummary selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询库存汇总
     */
    OzonProductStockSummary selectByProductId(@Param("productId") Long productId);

    /**
     * 查询所有库存汇总
     */
    List<OzonProductStockSummary> selectAll();

    /**
     * 根据条件查询库存汇总列表
     */
    List<OzonProductStockSummary> selectByCondition(OzonProductStockSummary condition);

    /**
     * 查询有库存的商品
     */
    List<OzonProductStockSummary> selectHasStock();

    /**
     * 查询无库存的商品
     */
    List<OzonProductStockSummary> selectNoStock();

    /**
     * 插入库存汇总
     */
    int insert(OzonProductStockSummary summary);

    /**
     * 根据ID更新库存汇总
     */
    int updateById(OzonProductStockSummary summary);

    /**
     * 根据商品ID更新库存汇总
     */
    int updateByProductId(OzonProductStockSummary summary);

    /**
     * 根据ID删除库存汇总
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据商品ID删除库存汇总
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 批量删除库存汇总
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 统计库存汇总数量
     */
    long count();

    /**
     * 统计有库存商品数量
     */
    long countHasStock();
}