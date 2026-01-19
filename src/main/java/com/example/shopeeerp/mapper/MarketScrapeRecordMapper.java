package com.example.shopeeerp.mapper;

import com.example.shopeeerp.pojo.MarketScrapeRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketScrapeRecordMapper {

    int insert(MarketScrapeRecord record);
}
