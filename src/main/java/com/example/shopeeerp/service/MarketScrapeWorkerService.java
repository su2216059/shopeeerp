package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.MarketScrapeWorker;

import java.util.List;

public interface MarketScrapeWorkerService {

    /**
     * Worker注册（如果已存在则更新）
     */
    boolean registerWorker(MarketScrapeWorker worker);

    /**
     * Worker心跳
     */
    boolean heartbeat(String workerId);

    /**
     * 更新Worker状态
     */
    boolean updateWorkerStatus(String workerId, String status, Long taskId);

    /**
     * 查询在线Worker列表
     */
    List<MarketScrapeWorker> getOnlineWorkers();

    /**
     * 标记离线Worker（最后心跳超过5分钟）
     */
    void markOfflineWorkers();

    /**
     * 更新Worker统计信息
     */
    boolean updateWorkerStats(String workerId, Integer totalTasks,
                             Integer successTasks, Integer failedTasks);
}
