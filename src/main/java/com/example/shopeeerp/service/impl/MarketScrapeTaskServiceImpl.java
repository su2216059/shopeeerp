package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketScrapeTaskMapper;
import com.example.shopeeerp.pojo.MarketScrapeTask;
import com.example.shopeeerp.service.MarketScrapeTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketScrapeTaskServiceImpl implements MarketScrapeTaskService {

    @Autowired
    private MarketScrapeTaskMapper mapper;

    @Value("${market.scrape.retry-delay-minutes:5}")
    private int retryDelayMinutes;

    @Override
    @Transactional
    public boolean enqueue(List<MarketScrapeTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        List<MarketScrapeTask> valid = new java.util.ArrayList<>();
        for (MarketScrapeTask task : tasks) {
            if (task == null || task.getUrl() == null || task.getUrl().trim().isEmpty()) {
                continue;
            }
            if (task.getStatus() == null) {
                task.setStatus("PENDING");
            }
            if (task.getPriority() == null) {
                task.setPriority(0);
            }
            if (task.getMaxRetries() == null) {
                task.setMaxRetries(3);
            }
            if (task.getRetryCount() == null) {
                task.setRetryCount(0);
            }
            if (task.getScheduledAt() == null) {
                task.setScheduledAt(now);
            }
            task.setCreatedAt(now);
            task.setUpdatedAt(now);
            valid.add(task);
        }
        if (valid.isEmpty()) {
            return false;
        }
        return mapper.insertBatch(valid) > 0;
    }

    @Override
    @Transactional
    public List<MarketScrapeTask> pull(String workerId, int limit) {
        if (workerId == null || workerId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        int size = limit > 0 ? limit : 50;
        LocalDateTime now = LocalDateTime.now();

        // Use row-level locks to avoid multiple workers claiming the same tasks.
        List<MarketScrapeTask> tasks = mapper.selectPendingForUpdate(now, size);
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = tasks.stream()
                .map(MarketScrapeTask::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        mapper.markInProgress(ids, workerId, now, now);
        tasks.forEach(task -> {
            task.setStatus("IN_PROGRESS");
            task.setLockOwner(workerId);
            task.setLockAt(now);
            task.setUpdatedAt(now);
        });
        return tasks;
    }

    @Override
    @Transactional
    public boolean reportSuccess(Long id, LocalDateTime fetchedAt) {
        if (id == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime doneAt = fetchedAt != null ? fetchedAt : now;
        return mapper.markSuccess(id, doneAt, now) > 0;
    }

    @Override
    @Transactional
    public boolean reportFailure(Long id, String errorMessage) {
        if (id == null) {
            return false;
        }
        int delayMinutes = retryDelayMinutes > 0 ? retryDelayMinutes : 5;
        return mapper.markFailure(id, errorMessage, LocalDateTime.now(), delayMinutes) > 0;
    }

    @Override
    @Transactional
    public boolean updateProgress(Long taskId, String workerId, String progressJson) {
        if (taskId == null || workerId == null || workerId.trim().isEmpty()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return mapper.updateProgress(taskId, workerId, progressJson, now) > 0;
    }

    @Override
    @Transactional
    public boolean completeTask(Long taskId, String workerId, String status,
                               Integer scrapedCount, Integer savedCount,
                               Integer skippedCount, String errorMessage) {
        if (taskId == null || workerId == null || workerId.trim().isEmpty()) {
            return false;
        }

        // 验证状态
        if (status == null || status.trim().isEmpty()) {
            status = "DONE";
        }
        if (!status.equals("DONE") && !status.equals("FAILED")) {
            status = "DONE";
        }

        LocalDateTime now = LocalDateTime.now();
        return mapper.completeTask(taskId, workerId, status, now, errorMessage, now) > 0;
    }

    @Override
    @Transactional
    public int releaseTimeoutTasks(int timeoutMinutes) {
        if (timeoutMinutes <= 0) {
            timeoutMinutes = 30;  // ??30??
        }
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(timeoutMinutes);
        LocalDateTime now = LocalDateTime.now();
        int released = mapper.releaseTimeoutTasks(timeout, now);
        int failed = mapper.markTimeoutFailed(timeout, now);
        return released + failed;
    }


    @Override
    public List<MarketScrapeTask> listTasks(Integer limit, String status) {
        int size = (limit != null && limit > 0) ? limit : 100;
        return mapper.selectTasks(status, size);
    }
}
