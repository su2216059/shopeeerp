package com.example.shopeeerp.service;

import com.example.shopeeerp.pojo.MarketSalesEstimate;
import com.example.shopeeerp.pojo.MarketTrendSignal;

import java.time.LocalDate;
import java.util.List;

/**
 * 市场销量估算服务
 * 
 * 基于 v1_review_rank 模型:
 * - 评论增量法计算销量区间
 * - 排名权重修正
 * - 置信度评分
 */
public interface MarketSalesEstimateService {

    /**
     * 为单个商品计算销量估算（指定周期）
     * @param platform 平台
     * @param platformProductId 商品ID
     * @param periodType 周期类型 daily/weekly/monthly
     * @param periodStart 周期开始日期
     * @param periodEnd 周期结束日期
     * @return 销量估算结果
     */
    MarketSalesEstimate calculateForProduct(String platform, String platformProductId,
                                            String periodType, LocalDate periodStart, LocalDate periodEnd);

    /**
     * 批量计算所有商品的销量估算
     * @param platform 平台
     * @param periodType 周期类型
     * @param periodStart 周期开始日期
     * @param periodEnd 周期结束日期
     * @return 处理结果
     */
    BatchResult calculateBatch(String platform, String periodType, LocalDate periodStart, LocalDate periodEnd);

    /**
     * 计算并保存每日销量估算（定时任务调用）
     * @param platform 平台
     * @param date 日期
     * @return 处理结果
     */
    BatchResult calculateDaily(String platform, LocalDate date);

    /**
     * 计算并保存每周销量估算
     * @param platform 平台
     * @param weekEndDate 周结束日期
     * @return 处理结果
     */
    BatchResult calculateWeekly(String platform, LocalDate weekEndDate);

    /**
     * 计算并保存每月销量估算
     * @param platform 平台
     * @param monthEndDate 月结束日期
     * @return 处理结果
     */
    BatchResult calculateMonthly(String platform, LocalDate monthEndDate);

    /**
     * 查询商品的最新销量估算
     */
    MarketSalesEstimate getLatestEstimate(String platform, String platformProductId, String periodType);

    /**
     * 查询商品的销量估算历史
     */
    List<MarketSalesEstimate> getEstimateHistory(String platform, String platformProductId, String periodType, int limit);

    /**
     * 计算商品的趋势信号
     */
    MarketTrendSignal calculateTrendSignal(String platform, String platformProductId, LocalDate signalDate);

    /**
     * 批量计算趋势信号
     */
    BatchResult calculateTrendSignalBatch(String platform, LocalDate signalDate);

    /**
     * 批量处理结果
     */
    class BatchResult {
        private int totalProducts;
        private int processed;
        private int skipped;
        private int errors;

        public BatchResult() {}

        public BatchResult(int totalProducts, int processed, int skipped, int errors) {
            this.totalProducts = totalProducts;
            this.processed = processed;
            this.skipped = skipped;
            this.errors = errors;
        }

        public int getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(int totalProducts) {
            this.totalProducts = totalProducts;
        }

        public int getProcessed() {
            return processed;
        }

        public void setProcessed(int processed) {
            this.processed = processed;
        }

        public int getSkipped() {
            return skipped;
        }

        public void setSkipped(int skipped) {
            this.skipped = skipped;
        }

        public int getErrors() {
            return errors;
        }

        public void setErrors(int errors) {
            this.errors = errors;
        }
    }
}
