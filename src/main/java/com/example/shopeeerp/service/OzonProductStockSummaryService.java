package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProductStockSummary;

import java.util.List;

/**
 * Ozon商品库存汇总Service接口
 */
public interface OzonProductStockSummaryService {

    /**
     * 根据ID查询库存汇总
     */
    OzonProductStockSummary getById(Long id);

    /**
     * 根据商品ID查询库存汇总
     */
    OzonProductStockSummary getByProductId(Long productId);

    /**
     * 查询所有库存汇总
     */
    List<OzonProductStockSummary> getAll();

    /**
     * 根据条件查询库存汇总列表
     */
    List<OzonProductStockSummary> getByCondition(OzonProductStockSummary condition);

    /**
     * 查询有库存的商品
     */
    List<OzonProductStockSummary> getHasStock();

    /**
     * 查询无库存的商品
     */
    List<OzonProductStockSummary> getNoStock();

    /**
     * 保存库存汇总
     */
    boolean save(OzonProductStockSummary summary);

    /**
     * 更新库存汇总
     */
    boolean update(OzonProductStockSummary summary);

    /**
     * 根据ID删除库存汇总
     */
    boolean removeById(Long id);

    /**
     * 根据商品ID删除库存汇总
     */
    boolean removeByProductId(Long productId);

    /**
     * 批量删除库存汇总
     */
    boolean removeBatch(List<Long> ids);

    /**
     * 统计库存汇总数量
     */
    long count();

    /**
     * 统计有库存商品数量
     */
    long countHasStock();
}