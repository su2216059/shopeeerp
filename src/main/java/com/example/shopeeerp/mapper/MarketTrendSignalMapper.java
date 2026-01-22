package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketTrendSignal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MarketTrendSignalMapper {

    /**
     * 按唯一键查询
     */
    MarketTrendSignal selectByUnique(@Param("platform") String platform,
                                     @Param("platformProductId") String platformProductId,
                                     @Param("signalDate") LocalDate signalDate);

    /**
     * 插入或更新
     */
    int upsert(MarketTrendSignal signal);

    /**
     * 查询商品的最新趋势信号
     */
    MarketTrendSignal selectLatestByProduct(@Param("platform") String platform,
                                            @Param("platformProductId") String platformProductId);

    /**
     * 查询某日期所有商品的趋势信号
     */
    List<MarketTrendSignal> selectByDate(@Param("platform") String platform,
                                         @Param("signalDate") LocalDate signalDate);

    /**
     * 查询商品的历史趋势信号
     */
    List<MarketTrendSignal> selectHistoryByProduct(@Param("platform") String platform,
                                                   @Param("platformProductId") String platformProductId,
                                                   @Param("limit") int limit);

    /**
     * 查询热门上升趋势商品（按7天趋势降序）
     */
    List<MarketTrendSignal> selectTopTrending(@Param("platform") String platform,
                                              @Param("signalDate") LocalDate signalDate,
                                              @Param("limit") int limit);

    /**
     * 查询排名上升最快的商品
     */
    List<MarketTrendSignal> selectTopRankRising(@Param("platform") String platform,
                                                @Param("signalDate") LocalDate signalDate,
                                                @Param("limit") int limit);

    /**
     * 查询评论增长最快的商品
     */
    List<MarketTrendSignal> selectTopReviewVelocity(@Param("platform") String platform,
                                                    @Param("signalDate") LocalDate signalDate,
                                                    @Param("limit") int limit);
}
