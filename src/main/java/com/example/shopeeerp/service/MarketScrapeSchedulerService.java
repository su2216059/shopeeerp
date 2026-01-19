package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.MarketScrapeScheduler;

public interface MarketScrapeSchedulerService {

    boolean heartbeat(MarketScrapeScheduler scheduler);
}
