package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.MarketScrapeTask;

import java.util.List;

public interface MarketScrapeTaskService {

    boolean enqueue(List<MarketScrapeTask> tasks);

    List<MarketScrapeTask> pull(String workerId, int limit);

    boolean reportSuccess(Long id, java.time.LocalDateTime fetchedAt);

    boolean reportFailure(Long id, String errorMessage);

    /**
     * 更新任务进度
     */
    boolean updateProgress(Long taskId, String workerId, String progressJson);

    /**
     * 完成任务（带详细结果）
     */
    boolean completeTask(Long taskId, String workerId, String status,
                        Integer scrapedCount, Integer savedCount,
                        Integer skippedCount, String errorMessage);

    /**
     * 释放超时任务
     */
    int releaseTimeoutTasks(int timeoutMinutes);

    /**
     * 查询任务列表
     */
    List<MarketScrapeTask> listTasks(Integer limit, String status);
}
