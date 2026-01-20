package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketSalesEstimate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MarketSalesEstimateMapper {

    /**
     * 按唯一键查询
     */
    MarketSalesEstimate selectByUnique(@Param("platform") String platform,
                                       @Param("platformProductId") String platformProductId,
                                       @Param("periodType") String periodType,
                                       @Param("periodStart") LocalDate periodStart,
                                       @Param("periodEnd") LocalDate periodEnd);

    /**
     * 插入或更新
     */
    int upsert(MarketSalesEstimate estimate);

    /**
     * 查询商品的最新销量估算
     */
    MarketSalesEstimate selectLatestByProduct(@Param("platform") String platform,
                                              @Param("platformProductId") String platformProductId,
                                              @Param("periodType") String periodType);

    /**
     * 查询某时间段内所有商品的销量估算
     */
    List<MarketSalesEstimate> selectByPeriod(@Param("platform") String platform,
                                             @Param("periodType") String periodType,
                                             @Param("periodStart") LocalDate periodStart,
                                             @Param("periodEnd") LocalDate periodEnd);

    /**
     * 查询商品的历史销量估算
     */
    List<MarketSalesEstimate> selectHistoryByProduct(@Param("platform") String platform,
                                                     @Param("platformProductId") String platformProductId,
                                                     @Param("periodType") String periodType,
                                                     @Param("limit") int limit);
}
