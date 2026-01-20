package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketProductSnapshotMapper;
import com.example.shopeeerp.mapper.MarketSalesEstimateMapper;
import com.example.shopeeerp.mapper.MarketTrendSignalMapper;
import com.example.shopeeerp.pojo.MarketProductSnapshot;
import com.example.shopeeerp.pojo.MarketSalesEstimate;
import com.example.shopeeerp.pojo.MarketTrendSignal;
import com.example.shopeeerp.service.MarketSalesEstimateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 销量估算服务实现
 * 
 * 算法说明（v1_review_rank）:
 * 
 * 1. 评论增量法（核心）:
 *    review_delta = review_count(end) - review_count(start)
 *    estimated_sales_min = review_delta / 0.06  (6%评论率)
 *    estimated_sales_max = review_delta / 0.02  (2%评论率)
 * 
 * 2. 排名权重修正:
 *    rank <= 10  -> 1.4
 *    rank <= 50  -> 1.2
 *    rank <= 200 -> 1.0
 *    else        -> 0.8
 * 
 * 3. 置信度评分:
 *    = 0.4 * data_density_score
 *    + 0.3 * review_stability_score
 *    + 0.3 * stock_consistency_score
 */
@Service
public class MarketSalesEstimateServiceImpl implements MarketSalesEstimateService {

    private static final Logger log = LoggerFactory.getLogger(MarketSalesEstimateServiceImpl.class);

    // 评论转化率范围
    private static final double REVIEW_RATE_MIN = 0.02;  // 2% 最低评论率 -> 最高销量
    private static final double REVIEW_RATE_MAX = 0.06;  // 6% 最高评论率 -> 最低销量

    // 估算模型版本
    private static final String ESTIMATION_MODEL = "v1_review_rank";

    @Autowired
    private MarketProductSnapshotMapper snapshotMapper;

    @Autowired
    private MarketSalesEstimateMapper estimateMapper;

    @Autowired
    private MarketTrendSignalMapper trendSignalMapper;

    @Override
    @Transactional
    public MarketSalesEstimate calculateForProduct(String platform, String platformProductId,
                                                   String periodType, LocalDate periodStart, LocalDate periodEnd) {
        // 获取周期内的快照数据
        List<MarketProductSnapshot> snapshots = snapshotMapper.selectByProductAndDateRange(
                platform, platformProductId, periodStart, periodEnd);

        if (snapshots == null || snapshots.isEmpty()) {
            log.debug("No snapshots found for product {} in period {}-{}", platformProductId, periodStart, periodEnd);
            return null;
        }

        // 获取首尾快照
        MarketProductSnapshot firstSnapshot = snapshots.get(0);
        MarketProductSnapshot lastSnapshot = snapshots.get(snapshots.size() - 1);

        // 计算评论增量
        int reviewDelta = 0;
        if (firstSnapshot.getReviewCount() != null && lastSnapshot.getReviewCount() != null) {
            reviewDelta = lastSnapshot.getReviewCount() - firstSnapshot.getReviewCount();
        }

        // 如果评论没有增量，使用绝对值估算
        if (reviewDelta <= 0 && lastSnapshot.getReviewCount() != null && lastSnapshot.getReviewCount() > 0) {
            // 对于无增量的情况，基于总评论数估算周期内销量
            long periodDays = ChronoUnit.DAYS.between(periodStart, periodEnd) + 1;
            double dailyFactor = periodDays / 365.0;  // 假设评论是年累计
            reviewDelta = (int) (lastSnapshot.getReviewCount() * dailyFactor);
        }

        // 评论增量法计算销量区间
        int estimatedMin = (int) Math.round(reviewDelta / REVIEW_RATE_MAX);
        int estimatedMax = (int) Math.round(reviewDelta / REVIEW_RATE_MIN);

        // 确保最小值不为负
        estimatedMin = Math.max(0, estimatedMin);
        estimatedMax = Math.max(estimatedMin, estimatedMax);

        // 排名权重修正
        double rankWeight = calculateRankWeight(lastSnapshot.getCategoryRank());
        estimatedMin = (int) Math.round(estimatedMin * rankWeight);
        estimatedMax = (int) Math.round(estimatedMax * rankWeight);

        // 计算中位值
        int estimatedMid = (estimatedMin + estimatedMax) / 2;

        // 计算置信度
        int confidenceScore = calculateConfidenceScore(platform, platformProductId, periodStart, periodEnd, snapshots);

        // 构建结果
        MarketSalesEstimate estimate = new MarketSalesEstimate();
        estimate.setPlatform(platform);
        estimate.setPlatformProductId(platformProductId);
        estimate.setPeriodType(periodType);
        estimate.setPeriodStart(periodStart);
        estimate.setPeriodEnd(periodEnd);
        estimate.setEstimatedSalesMin(estimatedMin);
        estimate.setEstimatedSalesMax(estimatedMax);
        estimate.setEstimatedSalesMid(estimatedMid);
        estimate.setEstimationModel(ESTIMATION_MODEL);
        estimate.setConfidenceScore(confidenceScore);
        estimate.setCreatedAt(LocalDateTime.now());

        // 保存
        estimateMapper.upsert(estimate);

        log.debug("Calculated sales estimate for {}: {} - {} (confidence: {})",
                platformProductId, estimatedMin, estimatedMax, confidenceScore);

        return estimate;
    }

    /**
     * 计算排名权重
     */
    private double calculateRankWeight(Integer categoryRank) {
        if (categoryRank == null) {
            return 1.0;
        }
        if (categoryRank <= 10) {
            return 1.4;
        } else if (categoryRank <= 50) {
            return 1.2;
        } else if (categoryRank <= 200) {
            return 1.0;
        } else {
            return 0.8;
        }
    }

    /**
     * 计算置信度评分
     * = 0.4 * 数据密度 + 0.3 * 评论稳定性 + 0.3 * 库存一致性
     */
    private int calculateConfidenceScore(String platform, String platformProductId,
                                         LocalDate periodStart, LocalDate periodEnd,
                                         List<MarketProductSnapshot> snapshots) {
        long totalDays = ChronoUnit.DAYS.between(periodStart, periodEnd) + 1;
        int snapshotDays = snapshots.size();

        // 1. 数据密度分数（采样天数 / 总天数）
        double densityRatio = (double) snapshotDays / totalDays;
        int dataDensityScore = (int) Math.min(100, densityRatio * 100);

        // 2. 评论稳定性分数（检查是否有异常增长）
        int reviewStabilityScore = calculateReviewStabilityScore(snapshots);

        // 3. 库存一致性分数（检查断货频率）
        int stockConsistencyScore = calculateStockConsistencyScore(snapshots);

        // 加权计算最终置信度
        int finalScore = (int) (0.4 * dataDensityScore + 0.3 * reviewStabilityScore + 0.3 * stockConsistencyScore);

        return Math.min(100, Math.max(0, finalScore));
    }

    /**
     * 计算评论稳定性分数
     */
    private int calculateReviewStabilityScore(List<MarketProductSnapshot> snapshots) {
        if (snapshots.size() < 2) {
            return 50;  // 数据不足，返回中等分数
        }

        int abnormalCount = 0;
        Integer prevReviewCount = null;

        for (MarketProductSnapshot snapshot : snapshots) {
            if (snapshot.getReviewCount() == null) continue;

            if (prevReviewCount != null) {
                int delta = snapshot.getReviewCount() - prevReviewCount;
                // 如果单日增长超过50条评论，认为异常
                if (delta > 50) {
                    abnormalCount++;
                }
            }
            prevReviewCount = snapshot.getReviewCount();
        }

        // 异常比例越低，分数越高
        double abnormalRatio = (double) abnormalCount / snapshots.size();
        return (int) (100 * (1 - abnormalRatio));
    }

    /**
     * 计算库存一致性分数
     */
    private int calculateStockConsistencyScore(List<MarketProductSnapshot> snapshots) {
        if (snapshots.isEmpty()) {
            return 50;
        }

        int outOfStockCount = 0;
        for (MarketProductSnapshot snapshot : snapshots) {
            if ("out_of_stock".equals(snapshot.getAvailabilityStatus())) {
                outOfStockCount++;
            }
        }

        // 断货比例越低，分数越高
        double outOfStockRatio = (double) outOfStockCount / snapshots.size();
        return (int) (100 * (1 - outOfStockRatio));
    }

    @Override
    @Transactional
    public BatchResult calculateBatch(String platform, String periodType, LocalDate periodStart, LocalDate periodEnd) {
        log.info("Starting batch calculation for {} {} from {} to {}", platform, periodType, periodStart, periodEnd);

        // 获取这个周期内有数据的所有商品
        List<String> productIds = snapshotMapper.selectDistinctProductIds(platform, periodStart, periodEnd);

        int processed = 0;
        int skipped = 0;
        int errors = 0;

        for (String productId : productIds) {
            try {
                MarketSalesEstimate result = calculateForProduct(platform, productId, periodType, periodStart, periodEnd);
                if (result != null) {
                    processed++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                log.error("Error calculating estimate for product {}: {}", productId, e.getMessage());
                errors++;
            }
        }

        log.info("Batch calculation completed: total={}, processed={}, skipped={}, errors={}",
                productIds.size(), processed, skipped, errors);

        return new BatchResult(productIds.size(), processed, skipped, errors);
    }

    @Override
    @Transactional
    public BatchResult calculateDaily(String platform, LocalDate date) {
        return calculateBatch(platform, "daily", date, date);
    }

    @Override
    @Transactional
    public BatchResult calculateWeekly(String platform, LocalDate weekEndDate) {
        LocalDate weekStart = weekEndDate.minusDays(6);
        return calculateBatch(platform, "weekly", weekStart, weekEndDate);
    }

    @Override
    @Transactional
    public BatchResult calculateMonthly(String platform, LocalDate monthEndDate) {
        LocalDate monthStart = monthEndDate.withDayOfMonth(1);
        return calculateBatch(platform, "monthly", monthStart, monthEndDate);
    }

    @Override
    public MarketSalesEstimate getLatestEstimate(String platform, String platformProductId, String periodType) {
        return estimateMapper.selectLatestByProduct(platform, platformProductId, periodType);
    }

    @Override
    public List<MarketSalesEstimate> getEstimateHistory(String platform, String platformProductId, String periodType, int limit) {
        return estimateMapper.selectHistoryByProduct(platform, platformProductId, periodType, limit);
    }

    @Override
    @Transactional
    public MarketTrendSignal calculateTrendSignal(String platform, String platformProductId, LocalDate signalDate) {
        // 获取7天前和30天前的数据
        LocalDate date7dAgo = signalDate.minusDays(7);
        LocalDate date30dAgo = signalDate.minusDays(30);

        // 获取当前和历史快照
        MarketProductSnapshot currentSnapshot = snapshotMapper.selectByUnique(platform, platformProductId, signalDate);
        MarketProductSnapshot snapshot7dAgo = snapshotMapper.selectByUnique(platform, platformProductId, date7dAgo);
        MarketProductSnapshot snapshot30dAgo = snapshotMapper.selectByUnique(platform, platformProductId, date30dAgo);

        if (currentSnapshot == null) {
            // 尝试获取最近的快照
            currentSnapshot = snapshotMapper.selectLatestByProduct(platform, platformProductId);
        }

        if (currentSnapshot == null) {
            log.debug("No snapshot found for product {}", platformProductId);
            return null;
        }

        MarketTrendSignal signal = new MarketTrendSignal();
        signal.setPlatform(platform);
        signal.setPlatformProductId(platformProductId);
        signal.setSignalDate(signalDate);
        signal.setCreatedAt(LocalDateTime.now());

        // 计算7天趋势
        if (snapshot7dAgo != null && currentSnapshot.getReviewCount() != null && snapshot7dAgo.getReviewCount() != null) {
            int reviewDelta7d = currentSnapshot.getReviewCount() - snapshot7dAgo.getReviewCount();
            if (snapshot7dAgo.getReviewCount() > 0) {
                double trend7d = (double) reviewDelta7d / snapshot7dAgo.getReviewCount() * 100;
                signal.setTrend7d(BigDecimal.valueOf(trend7d).setScale(2, RoundingMode.HALF_UP));
            }

            // 评论增长速度（每日）
            signal.setReviewVelocity(BigDecimal.valueOf(reviewDelta7d / 7.0).setScale(2, RoundingMode.HALF_UP));
        }

        // 计算30天趋势
        if (snapshot30dAgo != null && currentSnapshot.getReviewCount() != null && snapshot30dAgo.getReviewCount() != null) {
            int reviewDelta30d = currentSnapshot.getReviewCount() - snapshot30dAgo.getReviewCount();
            if (snapshot30dAgo.getReviewCount() > 0) {
                double trend30d = (double) reviewDelta30d / snapshot30dAgo.getReviewCount() * 100;
                signal.setTrend30d(BigDecimal.valueOf(trend30d).setScale(2, RoundingMode.HALF_UP));
            }
        }

        // 计算7天排名变化
        if (snapshot7dAgo != null && currentSnapshot.getCategoryRank() != null && snapshot7dAgo.getCategoryRank() != null) {
            // 排名下降（数值变大）为负，排名上升（数值变小）为正
            signal.setRankChange7d(snapshot7dAgo.getCategoryRank() - currentSnapshot.getCategoryRank());
        }

        // 计算库存风险等级
        String stockRiskLevel = calculateStockRiskLevel(platform, platformProductId, signalDate);
        signal.setStockRiskLevel(stockRiskLevel);

        // 保存
        trendSignalMapper.upsert(signal);

        return signal;
    }

    /**
     * 计算库存风险等级
     */
    private String calculateStockRiskLevel(String platform, String platformProductId, LocalDate signalDate) {
        LocalDate startDate = signalDate.minusDays(7);
        List<MarketProductSnapshot> recentSnapshots = snapshotMapper.selectByProductAndDateRange(
                platform, platformProductId, startDate, signalDate);

        if (recentSnapshots.isEmpty()) {
            return "unknown";
        }

        int outOfStockCount = 0;
        for (MarketProductSnapshot snapshot : recentSnapshots) {
            if ("out_of_stock".equals(snapshot.getAvailabilityStatus())) {
                outOfStockCount++;
            }
        }

        double outOfStockRatio = (double) outOfStockCount / recentSnapshots.size();

        if (outOfStockRatio >= 0.5) {
            return "high";
        } else if (outOfStockRatio >= 0.2) {
            return "medium";
        } else {
            return "low";
        }
    }

    @Override
    @Transactional
    public BatchResult calculateTrendSignalBatch(String platform, LocalDate signalDate) {
        log.info("Starting trend signal batch calculation for {} on {}", platform, signalDate);

        LocalDate startDate = signalDate.minusDays(30);
        List<String> productIds = snapshotMapper.selectDistinctProductIds(platform, startDate, signalDate);

        int processed = 0;
        int skipped = 0;
        int errors = 0;

        for (String productId : productIds) {
            try {
                MarketTrendSignal result = calculateTrendSignal(platform, productId, signalDate);
                if (result != null) {
                    processed++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                log.error("Error calculating trend signal for product {}: {}", productId, e.getMessage());
                errors++;
            }
        }

        log.info("Trend signal batch completed: total={}, processed={}, skipped={}, errors={}",
                productIds.size(), processed, skipped, errors);

        return new BatchResult(productIds.size(), processed, skipped, errors);
    }
}
