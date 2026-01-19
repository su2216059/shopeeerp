package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketScrapeRecordMapper;
import com.example.shopeeerp.pojo.MarketScrapeRecord;
import com.example.shopeeerp.service.MarketScrapeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketScrapeRecordServiceImpl implements MarketScrapeRecordService {

    @Autowired
    private MarketScrapeRecordMapper mapper;

    @Override
    @Transactional
    public boolean save(MarketScrapeRecord record) {
        if (record == null) {
            return false;
        }
        return mapper.insert(record) > 0;
    }
}
