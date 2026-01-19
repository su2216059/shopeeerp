package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketProductSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MarketProductSnapshotMapper {

    MarketProductSnapshot selectByUnique(@Param("platform") String platform,
                                         @Param("platformProductId") String platformProductId,
                                         @Param("snapshotDate") java.time.LocalDate snapshotDate);

    int upsert(MarketProductSnapshot snapshot);
}
