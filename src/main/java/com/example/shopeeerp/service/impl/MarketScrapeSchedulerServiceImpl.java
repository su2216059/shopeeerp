package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketScrapeSchedulerMapper;
import com.example.shopeeerp.pojo.MarketScrapeScheduler;
import com.example.shopeeerp.service.MarketScrapeSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MarketScrapeSchedulerServiceImpl implements MarketScrapeSchedulerService {

    @Autowired
    private MarketScrapeSchedulerMapper mapper;

    @Override
    @Transactional
    public boolean heartbeat(MarketScrapeScheduler scheduler) {
        if (scheduler == null || scheduler.getWorkerId() == null || scheduler.getWorkerId().trim().isEmpty()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (scheduler.getLastHeartbeat() == null) {
            scheduler.setLastHeartbeat(now);
        }
        scheduler.setCreatedAt(now);
        scheduler.setUpdatedAt(now);
        return mapper.upsert(scheduler) > 0;
    }
}
