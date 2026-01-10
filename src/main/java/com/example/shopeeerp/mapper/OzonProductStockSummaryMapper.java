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
     * 插入库存汇总记录
     */
    int insert(OzonProductStockSummary stockSummary);

    /**
     * 根据ID删除库存汇总记录
     */
    int deleteById(Long id);

    /**
     * 根据商品ID删除库存汇总记录
     */
    int deleteByProductId(Long productId);

    /**
     * 更新库存汇总记录
     */
    int update(OzonProductStockSummary stockSummary);

    /**
     * 插入或更新库存汇总记录
     */
    int insertOrUpdate(OzonProductStockSummary stockSummary);

    /**
     * 根据ID查询库存汇总记录
     */
    OzonProductStockSummary selectById(Long id);

    /**
     * 根据商品ID查询库存汇总记录
     */
    OzonProductStockSummary selectByProductId(Long productId);

    /**
     * 根据是否有库存查询记录
     */
    List<OzonProductStockSummary> selectByHasStock(@Param("hasStock") Boolean hasStock);

    /**
     * 查询所有库存汇总记录
     */
    List<OzonProductStockSummary> selectAll();
}
