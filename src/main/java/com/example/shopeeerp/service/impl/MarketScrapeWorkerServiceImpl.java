package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketScrapeWorkerMapper;
import com.example.shopeeerp.pojo.MarketScrapeWorker;
import com.example.shopeeerp.service.MarketScrapeWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketScrapeWorkerServiceImpl implements MarketScrapeWorkerService {

    @Autowired
    private MarketScrapeWorkerMapper workerMapper;

    @Override
    @Transactional
    public boolean registerWorker(MarketScrapeWorker worker) {
        if (worker == null || worker.getWorkerId() == null || worker.getWorkerId().trim().isEmpty()) {
            return false;
        }

        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        if (worker.getStatus() == null) {
            worker.setStatus("idle");
        }
        if (worker.getTotalTasks() == null) {
            worker.setTotalTasks(0);
        }
        if (worker.getSuccessTasks() == null) {
            worker.setSuccessTasks(0);
        }
        if (worker.getFailedTasks() == null) {
            worker.setFailedTasks(0);
        }
        worker.setLastHeartbeat(now);
        worker.setRegisteredAt(now);
        worker.setUpdatedAt(now);

        // 使用ON DUPLICATE KEY UPDATE实现upsert
        return workerMapper.insert(worker) > 0;
    }

    @Override
    public boolean heartbeat(String workerId) {
        if (workerId == null || workerId.trim().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return workerMapper.updateHeartbeat(workerId, now) > 0;
    }

    @Override
    public boolean updateWorkerStatus(String workerId, String status, Long taskId) {
        if (workerId == null || workerId.trim().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return workerMapper.updateWorkerStatus(workerId, status, taskId, now) > 0;
    }

    @Override
    public List<MarketScrapeWorker> getOnlineWorkers() {
        // 5分钟内有心跳的Worker视为在线
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(5);
        return workerMapper.selectOnlineWorkers(timeout);
    }

    @Override
    @Transactional
    public void markOfflineWorkers() {
        // 5分钟内没有心跳的Worker标记为离线
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(5);
        LocalDateTime now = LocalDateTime.now();
        workerMapper.markOfflineWorkers(timeout, now);
    }

    @Override
    public boolean updateWorkerStats(String workerId, Integer totalTasks,
                                     Integer successTasks, Integer failedTasks) {
        if (workerId == null || workerId.trim().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return workerMapper.updateWorkerStats(workerId, totalTasks, successTasks, failedTasks, now) > 0;
    }
}
