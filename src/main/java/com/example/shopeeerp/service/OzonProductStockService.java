package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProductStock;

import java.util.List;

/**
 * Ozon商品库存明细Service接口
 */
public interface OzonProductStockService {

    /**
     * 根据ID查询库存明细
     */
    OzonProductStock getById(Long id);

    /**
     * 根据商品ID查询库存明细列表
     */
    List<OzonProductStock> getByProductId(Long productId);

    /**
     * 根据SKU查询库存明细
     */
    List<OzonProductStock> getBySku(Long sku);

    /**
     * 根据来源查询库存明细
     */
    List<OzonProductStock> getBySource(String source);

    /**
     * 根据商品ID和来源查询库存明细
     */
    OzonProductStock getByProductIdAndSource(Long productId, String source);

    /**
     * 查询所有库存明细
     */
    List<OzonProductStock> getAll();

    /**
     * 根据条件查询库存明细列表
     */
    List<OzonProductStock> getByCondition(OzonProductStock condition);

    /**
     * 保存库存明细
     */
    boolean save(OzonProductStock stock);

    /**
     * 批量保存库存明细
     */
    boolean saveBatch(List<OzonProductStock> stocks);

    /**
     * 更新库存明细
     */
    boolean update(OzonProductStock stock);

    /**
     * 根据ID删除库存明细
     */
    boolean removeById(Long id);

    /**
     * 根据商品ID删除库存明细
     */
    boolean removeByProductId(Long productId);

    /**
     * 根据SKU删除库存明细
     */
    boolean removeBySku(Long sku);

    /**
     * 根据商品ID和来源删除库存明细
     */
    boolean removeByProductIdAndSource(Long productId, String source);

    /**
     * 批量删除库存明细
     */
    boolean removeBatch(List<Long> ids);

    /**
     * 统计库存明细数量
     */
    long count();

    /**
     * 根据商品ID统计库存明细数量
     */
    long countByProductId(Long productId);

    /**
     * 计算商品总可用库存
     */
    int sumPresentByProductId(Long productId);

    /**
     * 计算商品总预留库存
     */
    int sumReservedByProductId(Long productId);
}