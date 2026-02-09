package com.example.shopeeerp.controller;

import com.example.shopeeerp.adapter.dto.ozon.CommonResult;
import com.example.shopeeerp.adapter.dto.ozon.ProfitQueryDto;
import com.example.shopeeerp.adapter.dto.ozon.ProfitResultResponse;
import com.example.shopeeerp.service.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @version: V1.0
 * @author: 苏航
 * @className: ProfitController
 * @packageName: com.example.shopeeerp.controller
 * @description:
 * @date: 2026/2/5 9:30
 */
@RestController
@RequestMapping("/profit")
@Slf4j
public class ProfitController {
    @Autowired
    ProfitService profitService;

    @Autowired @Qualifier("ioExecutor")
    private Executor ioExecutor;
    @GetMapping("/summary")
    public CommonResult<ProfitResultResponse> summary(@RequestParam Long shopId,
                                                      @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
                                                      @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate,
                                                      @RequestParam(required=false) String sku){
        ProfitQueryDto dto = new ProfitQueryDto();
        dto.setShopId(shopId);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setSku(sku);
        ProfitResultResponse summary = profitService.summary(dto);
        return CommonResult.success(summary);
    }

    @PostMapping("/calcByOrder")
    public CommonResult<ProfitResultResponse> calcByOrder(@RequestParam String orderId) {
        return CommonResult.success(profitService.calcByOrder(orderId));
    }

    @PostMapping("/calcByOrderAsync")
    public CommonResult<Void> calcAsync(@RequestParam String orderId) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("traceId",traceId);
        ioExecutor.execute(() -> {
            try {
                MDC.put("traceId",traceId);
                safeCalc(orderId,traceId);
            }finally {
                MDC.clear();
            }
           });
        return CommonResult.success(null);
    }


    private void safeCalc(String orderId,String traceId) {
        long start = System.currentTimeMillis();
        String thread = Thread.currentThread().getName();
        log.info("START calcByOrder, orderId={}, traceId={}, thread={}",
                orderId, traceId, thread);
        int maxRetry = 2;
        for (int i = 0; i <= maxRetry; i++) {
            try {
                // 给单次任务加“软超时”保护
                CompletableFuture<Void> f =
                        CompletableFuture.runAsync(() -> {
                            MDC.put("traceId",traceId);
                            calcByOrder(orderId);}, ioExecutor);


                f.get(5, TimeUnit.SECONDS);   // 超过 5 秒抛异常
                long cost = System.currentTimeMillis() - start;
                log.info("SUCCESS calcByOrder, orderId={}, traceId={}, cost={}ms",
                        orderId, traceId, cost);
                return; // 成功直接结束
            } catch (Exception e) {
                log.warn("FAILED calcByOrder, orderId={}, traceId={}, retry={}",
                        orderId, traceId, i, e);
                if (i == maxRetry) {
                    log.error("calcByOrder FINAL FAIL, orderId={}", orderId);
                }
            }
        }
        log.error("FINAL FAIL calcByOrder, orderId={}, traceId={}", orderId, traceId);
    }

}
