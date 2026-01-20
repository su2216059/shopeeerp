package com.example.shopeeerp.scheduler;

import com.example.shopeeerp.service.MarketSalesEstimateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 市场信号定时任务
 * 
 * 执行计划：
 * - 每天凌晨2点: 计算前一天的每日销量估算
 * - 每周一凌晨3点: 计算上周的每周销量估算
 * - 每月1日凌晨4点: 计算上月的每月销量估算
 * - 每天凌晨5点: 计算趋势信号
 */
@Component
public class MarketSignalScheduler {

    private static final Logger log = LoggerFactory.getLogger(MarketSignalScheduler.class);

    private static final String PLATFORM = "ozon";

    @Autowired
    private MarketSalesEstimateService estimateService;

    /**
     * 每天凌晨2点计算每日销量估算
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateDailyEstimates() {
        log.info("Starting daily sales estimate calculation...");

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            MarketSalesEstimateService.BatchResult result = estimateService.calculateDaily(PLATFORM, yesterday);

            log.info("Daily estimate completed: processed={}, skipped={}, errors={}",
                    result.getProcessed(), result.getSkipped(), result.getErrors());
        } catch (Exception e) {
            log.error("Error in daily estimate calculation", e);
        }
    }

    /**
     * 每周一凌晨3点计算每周销量估算
     */
    @Scheduled(cron = "0 0 3 * * MON")
    public void calculateWeeklyEstimates() {
        log.info("Starting weekly sales estimate calculation...");

        try {
            LocalDate lastSunday = LocalDate.now().minusDays(1);  // 上周日
            MarketSalesEstimateService.BatchResult result = estimateService.calculateWeekly(PLATFORM, lastSunday);

            log.info("Weekly estimate completed: processed={}, skipped={}, errors={}",
                    result.getProcessed(), result.getSkipped(), result.getErrors());
        } catch (Exception e) {
            log.error("Error in weekly estimate calculation", e);
        }
    }

    /**
     * 每月1日凌晨4点计算每月销量估算
     */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void calculateMonthlyEstimates() {
        log.info("Starting monthly sales estimate calculation...");

        try {
            LocalDate lastDayOfPrevMonth = LocalDate.now().minusDays(1);  // 上月最后一天
            MarketSalesEstimateService.BatchResult result = estimateService.calculateMonthly(PLATFORM, lastDayOfPrevMonth);

            log.info("Monthly estimate completed: processed={}, skipped={}, errors={}",
                    result.getProcessed(), result.getSkipped(), result.getErrors());
        } catch (Exception e) {
            log.error("Error in monthly estimate calculation", e);
        }
    }

    /**
     * 每天凌晨5点计算趋势信号
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void calculateTrendSignals() {
        log.info("Starting trend signal calculation...");

        try {
            LocalDate today = LocalDate.now();
            MarketSalesEstimateService.BatchResult result = estimateService.calculateTrendSignalBatch(PLATFORM, today);

            log.info("Trend signal completed: processed={}, skipped={}, errors={}",
                    result.getProcessed(), result.getSkipped(), result.getErrors());
        } catch (Exception e) {
            log.error("Error in trend signal calculation", e);
        }
    }
}
