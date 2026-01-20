package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.MarketSalesEstimate;
import com.example.shopeeerp.pojo.MarketTrendSignal;
import com.example.shopeeerp.service.MarketSalesEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 市场销量估算 API
 */
@RestController
@RequestMapping("/market/estimate")
@CrossOrigin(origins = "*")
public class MarketSalesEstimateController {

    @Autowired
    private MarketSalesEstimateService estimateService;

    /**
     * 查询商品的最新销量估算
     * GET /market/estimate/{platform}/{productId}?periodType=weekly
     */
    @GetMapping("/{platform}/{productId}")
    public ResponseEntity<?> getLatestEstimate(
            @PathVariable String platform,
            @PathVariable String productId,
            @RequestParam(defaultValue = "weekly") String periodType) {

        MarketSalesEstimate estimate = estimateService.getLatestEstimate(platform, productId, periodType);

        if (estimate == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(estimate);
    }

    /**
     * 查询商品的销量估算历史
     * GET /market/estimate/{platform}/{productId}/history?periodType=weekly&limit=10
     */
    @GetMapping("/{platform}/{productId}/history")
    public ResponseEntity<?> getEstimateHistory(
            @PathVariable String platform,
            @PathVariable String productId,
            @RequestParam(defaultValue = "weekly") String periodType,
            @RequestParam(defaultValue = "10") int limit) {

        List<MarketSalesEstimate> history = estimateService.getEstimateHistory(platform, productId, periodType, limit);

        return ResponseEntity.ok(history);
    }

    /**
     * 手动触发单个商品的销量估算计算
     * POST /market/estimate/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateForProduct(@RequestBody CalculateRequest request) {

        MarketSalesEstimate estimate = estimateService.calculateForProduct(
                request.getPlatform(),
                request.getProductId(),
                request.getPeriodType(),
                request.getPeriodStart(),
                request.getPeriodEnd()
        );

        if (estimate == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "No snapshot data available for this product and period");
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(estimate);
    }

    /**
     * 手动触发批量销量估算计算
     * POST /market/estimate/calculate-batch
     */
    @PostMapping("/calculate-batch")
    public ResponseEntity<?> calculateBatch(@RequestBody BatchCalculateRequest request) {

        MarketSalesEstimateService.BatchResult result = estimateService.calculateBatch(
                request.getPlatform(),
                request.getPeriodType(),
                request.getPeriodStart(),
                request.getPeriodEnd()
        );

        return ResponseEntity.ok(result);
    }

    /**
     * 手动触发每日计算
     * POST /market/estimate/calculate-daily?platform=ozon&date=2026-01-19
     */
    @PostMapping("/calculate-daily")
    public ResponseEntity<?> calculateDaily(
            @RequestParam(defaultValue = "ozon") String platform,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        MarketSalesEstimateService.BatchResult result = estimateService.calculateDaily(platform, date);
        return ResponseEntity.ok(result);
    }

    /**
     * 手动触发每周计算
     * POST /market/estimate/calculate-weekly?platform=ozon&weekEndDate=2026-01-19
     */
    @PostMapping("/calculate-weekly")
    public ResponseEntity<?> calculateWeekly(
            @RequestParam(defaultValue = "ozon") String platform,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndDate) {

        MarketSalesEstimateService.BatchResult result = estimateService.calculateWeekly(platform, weekEndDate);
        return ResponseEntity.ok(result);
    }

    /**
     * 手动触发趋势信号批量计算
     * POST /market/estimate/calculate-trend?platform=ozon&date=2026-01-19
     */
    @PostMapping("/calculate-trend")
    public ResponseEntity<?> calculateTrendSignals(
            @RequestParam(defaultValue = "ozon") String platform,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        MarketSalesEstimateService.BatchResult result = estimateService.calculateTrendSignalBatch(platform, date);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询商品的趋势信号
     * GET /market/estimate/{platform}/{productId}/trend
     */
    @GetMapping("/{platform}/{productId}/trend")
    public ResponseEntity<?> getTrendSignal(
            @PathVariable String platform,
            @PathVariable String productId) {

        MarketTrendSignal signal = estimateService.calculateTrendSignal(platform, productId, LocalDate.now());

        if (signal == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(signal);
    }

    // Request DTOs
    public static class CalculateRequest {
        private String platform;
        private String productId;
        private String periodType;
        private LocalDate periodStart;
        private LocalDate periodEnd;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getPeriodType() {
            return periodType;
        }

        public void setPeriodType(String periodType) {
            this.periodType = periodType;
        }

        public LocalDate getPeriodStart() {
            return periodStart;
        }

        public void setPeriodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
        }

        public LocalDate getPeriodEnd() {
            return periodEnd;
        }

        public void setPeriodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
        }
    }

    public static class BatchCalculateRequest {
        private String platform;
        private String periodType;
        private LocalDate periodStart;
        private LocalDate periodEnd;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getPeriodType() {
            return periodType;
        }

        public void setPeriodType(String periodType) {
            this.periodType = periodType;
        }

        public LocalDate getPeriodStart() {
            return periodStart;
        }

        public void setPeriodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
        }

        public LocalDate getPeriodEnd() {
            return periodEnd;
        }

        public void setPeriodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
        }
    }
}
