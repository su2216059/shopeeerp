package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProduct;

import java.util.List;

/**
 * Ozon商品Service接口
 */
public interface OzonProductService {

    /**
     * 根据ID查询商品
     */
    OzonProduct getById(Long id);

    /**
     * 根据offerId查询商品
     */
    OzonProduct getByOfferId(String offerId);

    /**
     * 根据SKU查询商品
     */
    OzonProduct getBySku(Long sku);

    /**
     * 查询所有商品
     */
    List<OzonProduct> getAll();

    /**
     * 根据条件查询商品列表
     */
    List<OzonProduct> getByCondition(OzonProduct condition);

    /**
     * 分页查询商品列表
     */
    List<OzonProduct> getByPage(int pageNum, int pageSize);

    /**
     * 保存商品
     */
    boolean save(OzonProduct product);

    /**
     * 批量保存商品
     */
    boolean saveBatch(List<OzonProduct> products);

    /**
     * 更新商品
     */
    boolean update(OzonProduct product);

    /**
     * 根据ID删除商品
     */
    boolean removeById(Long id);

    /**
     * 批量删除商品
     */
    boolean removeBatch(List<Long> ids);

    /**
     * 统计商品数量
     */
    long count();

    /**
     * 根据条件统计商品数量
     */
    long countByCondition(OzonProduct condition);
}