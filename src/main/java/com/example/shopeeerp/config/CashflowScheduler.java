package com.example.shopeeerp.config;

import com.example.shopeeerp.service.OzonCashflowSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class CashflowScheduler {

    @Autowired
    private OzonCashflowSyncService cashflowSyncService;

    /**
     * 每天凌晨0点同步前一日的财务报表
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailySync() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.plusDays(1).atStartOfDay();
        cashflowSyncService.sync(start, end);
    }
}
