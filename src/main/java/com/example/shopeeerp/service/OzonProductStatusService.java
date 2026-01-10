package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProductStatus;

import java.util.List;

/**
 * Ozon商品状态Service接口
 */
public interface OzonProductStatusService {

    /**
     * 根据ID查询状态
     */
    OzonProductStatus getById(Long id);

    /**
     * 根据商品ID查询状态
     */
    OzonProductStatus getByProductId(Long productId);

    /**
     * 查询所有状态
     */
    List<OzonProductStatus> getAll();

    /**
     * 根据条件查询状态列表
     */
    List<OzonProductStatus> getByCondition(OzonProductStatus condition);

    /**
     * 根据状态查询商品列表
     */
    List<OzonProductStatus> getByStatus(String status);

    /**
     * 根据审核状态查询商品列表
     */
    List<OzonProductStatus> getByModerateStatus(String moderateStatus);

    /**
     * 保存状态
     */
    boolean save(OzonProductStatus status);

    /**
     * 更新状态
     */
    boolean update(OzonProductStatus status);

    /**
     * 根据ID删除状态
     */
    boolean removeById(Long id);

    /**
     * 根据商品ID删除状态
     */
    boolean removeByProductId(Long productId);

    /**
     * 批量删除状态
     */
    boolean removeBatch(List<Long> ids);

    /**
     * 统计状态数量
     */
    long count();

    /**
     * 根据状态统计数量
     */
    long countByStatus(String status);
}