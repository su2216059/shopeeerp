package com.example.shopeeerp.scheduler;

import com.example.shopeeerp.service.MarketScrapeTaskService;
import com.example.shopeeerp.service.MarketScrapeWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class MarketScrapeScheduler {

    private static final Logger log = LoggerFactory.getLogger(MarketScrapeScheduler.class);

    @Autowired
    private MarketScrapeTaskService taskService;

    @Autowired
    private MarketScrapeWorkerService workerService;

    /**
     * 每5分钟释放超时任务
     * 超时时间设置为30分钟
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void releaseTimeoutTasks() {
        try {
            int released = taskService.releaseTimeoutTasks(30);
            if (released > 0) {
                log.info("Released {} timeout tasks", released);
            }
        } catch (Exception e) {
            log.error("Failed to release timeout tasks", e);
        }
    }

    /**
     * 每分钟标记离线Worker
     * 5分钟内没有心跳的Worker标记为离线
     */
    @Scheduled(cron = "0 * * * * ?")
    public void markOfflineWorkers() {
        try {
            workerService.markOfflineWorkers();
        } catch (Exception e) {
            log.error("Failed to mark offline workers", e);
        }
    }
}
