package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.MarketScrapeTask;

import java.util.List;

public interface MarketScrapeTaskService {

    boolean enqueue(List<MarketScrapeTask> tasks);

    List<MarketScrapeTask> pull(String workerId, int limit);

    boolean reportSuccess(Long id, java.time.LocalDateTime fetchedAt);

    boolean reportFailure(Long id, String errorMessage);
}
