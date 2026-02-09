package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.OzonProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ozon商品Service接口
 */
public interface OzonProductService {

    /**
     * 根据ID查询商品
     */
    OzonProduct getById(Long id, Long shopId);

    /**
     * 根据offerId查询商品
     */
    OzonProduct getByOfferId(String offerId, Long shopId);

    /**
     * 根据SKU查询商品
     */
    OzonProduct getBySku(Long sku, Long shopId);

    /**
     * 查询所有商品
     */
    List<OzonProduct> getAll(Long shopId);

    /**
     * 根据条件查询商品列表
     */
    List<OzonProduct> getByCondition(OzonProduct condition);

    /**
     * 根据筛选条件查询商品列表
     */
    List<OzonProduct> getByFilters(String title,
                                   Long shopId,
                                   String productCode,
                                   LocalDateTime createdFrom,
                                   LocalDateTime createdTo,
                                   String visibility);

    /**
     * 分页查询商品列表
     */
    List<OzonProduct> getByPage(int pageNum, int pageSize, Long shopId);

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
    boolean removeById(Long id, Long shopId);

    /**
     * 批量删除商品
     */
    boolean removeBatch(List<Long> ids, Long shopId);

    /**
     * 统计商品数量
     */
    long count(Long shopId);

    /**
     * 根据条件统计商品数量
     */
    long countByCondition(OzonProduct condition);

    /*
     * @author: 苏航
     * @methodName: 根据条件获取商品采购价
     * @Description:
     * @param:
     * @return:
     * @throws
     */
    BigDecimal getPurchasePrice(Long shopId, String sku);
}
