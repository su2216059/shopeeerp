package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketScrapeScheduler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MarketScrapeSchedulerMapper {

    int upsert(MarketScrapeScheduler scheduler);

    MarketScrapeScheduler selectByWorkerId(@Param("workerId") String workerId);
}
