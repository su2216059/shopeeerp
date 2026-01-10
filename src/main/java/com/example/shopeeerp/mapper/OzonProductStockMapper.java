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
     * 插入库存明细记录
     */
    int insert(OzonProductStock stock);

    /**
     * 批量插入库存明细记录
     */
    int batchInsert(@Param("list") List<OzonProductStock> stocks);

    /**
     * 根据ID删除库存明细记录
     */
    int deleteById(Long id);

    /**
     * 根据商品ID删除所有库存明细
     */
    int deleteByProductId(Long productId);

    /**
     * 更新库存明细记录
     */
    int update(OzonProductStock stock);

    /**
     * 插入或更新库存明细记录
     */
    int insertOrUpdate(OzonProductStock stock);

    /**
     * 根据ID查询库存明细记录
     */
    OzonProductStock selectById(Long id);

    /**
     * 根据商品ID查询所有库存明细
     */
    List<OzonProductStock> selectByProductId(Long productId);

    /**
     * 根据商品ID和来源查询库存明细
     */
    OzonProductStock selectByProductIdAndSource(@Param("productId") Long productId, @Param("source") String source);

    /**
     * 查询所有库存明细记录
     */
    List<OzonProductStock> selectAll();
}
