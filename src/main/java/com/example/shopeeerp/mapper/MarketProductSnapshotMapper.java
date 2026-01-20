package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketProductSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MarketProductSnapshotMapper {

    MarketProductSnapshot selectByUnique(@Param("platform") String platform,
                                         @Param("platformProductId") String platformProductId,
                                         @Param("snapshotDate") LocalDate snapshotDate);

    int upsert(MarketProductSnapshot snapshot);

    /**
     * 查询商品在指定日期范围内的快照
     */
    List<MarketProductSnapshot> selectByProductAndDateRange(@Param("platform") String platform,
                                                            @Param("platformProductId") String platformProductId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    /**
     * 查询有快照数据的所有商品ID（去重）
     */
    List<String> selectDistinctProductIds(@Param("platform") String platform,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    /**
     * 查询商品最早的快照
     */
    MarketProductSnapshot selectFirstByProduct(@Param("platform") String platform,
                                               @Param("platformProductId") String platformProductId);

    /**
     * 查询商品最新的快照
     */
    MarketProductSnapshot selectLatestByProduct(@Param("platform") String platform,
                                                @Param("platformProductId") String platformProductId);

    /**
     * 统计商品在日期范围内的快照天数
     */
    int countSnapshotDays(@Param("platform") String platform,
                         @Param("platformProductId") String platformProductId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate);
}
