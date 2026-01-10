package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProductImage;

import java.util.List;

/**
 * Ozon商品图片Service接口
 */
public interface OzonProductImageService {

    /**
     * 根据ID查询图片
     */
    OzonProductImage getById(Long id);

    /**
     * 根据商品ID查询图片列表
     */
    List<OzonProductImage> getByProductId(Long productId);

    /**
     * 查询所有图片
     */
    List<OzonProductImage> getAll();

    /**
     * 根据条件查询图片列表
     */
    List<OzonProductImage> getByCondition(OzonProductImage condition);

    /**
     * 保存图片
     */
    boolean save(OzonProductImage image);

    /**
     * 批量保存图片
     */
    boolean saveBatch(List<OzonProductImage> images);

    /**
     * 更新图片
     */
    boolean update(OzonProductImage image);

    /**
     * 根据ID删除图片
     */
    boolean removeById(Long id);

    /**
     * 根据商品ID删除图片
     */
    boolean removeByProductId(Long productId);

    /**
     * 批量删除图片
     */
    boolean removeBatch(List<Long> ids);

    /**
     * 统计图片数量
     */
    long count();

    /**
     * 根据商品ID统计图片数量
     */
    long countByProductId(Long productId);
}