package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketScrapeTaskMapper;
import com.example.shopeeerp.pojo.MarketScrapeTask;
import com.example.shopeeerp.service.MarketScrapeTaskService;
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
        return mapper.markFailure(id, errorMessage, LocalDateTime.now()) > 0;
    }
}
