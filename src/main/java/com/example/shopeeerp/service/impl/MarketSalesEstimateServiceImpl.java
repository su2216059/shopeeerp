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
 * Sales estimate based on sold_count.
 */

@Service
public class MarketSalesEstimateServiceImpl implements MarketSalesEstimateService {

    private static final Logger log = LoggerFactory.getLogger(MarketSalesEstimateServiceImpl.class);

    // 评论转化率范围 (Ozon 实际数据 - 按月计算)
    // estimation model version
    private static final String ESTIMATION_MODEL = "v3_ozon_sales";

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

        Integer resolvedSales = resolveSalesCount(snapshots, periodType, periodStart, periodEnd);
        if (resolvedSales == null) {
            log.debug("No sales data for product {} in period {}-{}", platformProductId, periodStart, periodEnd);
            return null;
        }

        int estimatedMin = Math.max(0, resolvedSales);
        int estimatedMid = estimatedMin;
        int estimatedMax = estimatedMin;

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
     * 根据价格获取评论率范围
     * 低价商品评论率高，高价商品评论率低
     * 
     * @return [低评论率, 中位评论率, 高评论率]
     */
    private Integer resolveSalesCount(List<MarketProductSnapshot> snapshots, String periodType,
                                      LocalDate periodStart, LocalDate periodEnd) {
        if (snapshots == null || snapshots.isEmpty()) {
            return null;
        }
        MarketProductSnapshot latest = snapshots.get(snapshots.size() - 1);
        Integer base = latest.getSoldCount();
        if (base == null) {
            return null;
        }
        String sourcePeriod = latest.getSalesPeriod();
        if (sourcePeriod == null || periodType == null || sourcePeriod.equalsIgnoreCase(periodType)) {
            return base;
        }
        int sourceDays = resolvePeriodDays(sourcePeriod, latest, periodEnd);
        int targetDays = (int) (ChronoUnit.DAYS.between(periodStart, periodEnd) + 1);
        if (sourceDays <= 0 || targetDays <= 0) {
            return base;
        }
        return (int) Math.round((double) base * targetDays / sourceDays);
    }

    private int resolvePeriodDays(String period, MarketProductSnapshot snapshot, LocalDate periodEnd) {
        if (period == null) {
            return 0;
        }
        String normalized = period.trim().toLowerCase();
        if ("weekly".equals(normalized)) {
            return 7;
        }
        if ("daily".equals(normalized)) {
            return 1;
        }
        if ("monthly".equals(normalized)) {
            LocalDate ref = periodEnd;
            if (snapshot.getSalesUpdateAt() != null) {
                ref = snapshot.getSalesUpdateAt().toLocalDate();
            }
            return ref.lengthOfMonth();
        }
        return 0;
    }

    private int calculateConfidenceScore(String platform, String platformProductId,
                                         LocalDate periodStart, LocalDate periodEnd,
                                         List<MarketProductSnapshot> snapshots) {
        long totalDays = ChronoUnit.DAYS.between(periodStart, periodEnd) + 1;
        int snapshotDays = snapshots.size();

        // 1. 数据密度分数（采样天数 / 总天数）
        double densityRatio = (double) snapshotDays / totalDays;
        int dataDensityScore = (int) Math.min(100, densityRatio * 100);

        // 2. 评论稳定性分数（检查是否有异常增长）
        int salesStabilityScore = calculateSalesStabilityScore(snapshots);

        // 3. 库存一致性分数（检查断货频率）
        int stockConsistencyScore = calculateStockConsistencyScore(snapshots);

        // 加权计算最终置信度
        int finalScore = (int) (0.4 * dataDensityScore + 0.4 * salesStabilityScore + 0.2 * stockConsistencyScore);

        return Math.min(100, Math.max(0, finalScore));
    }

    /**
     * 计算评论稳定性分数
     */
    private int calculateSalesStabilityScore(List<MarketProductSnapshot> snapshots) {
        if (snapshots.size() < 2) {
            return 50;
        }

        int abnormalCount = 0;
        Integer prevSales = null;

        for (MarketProductSnapshot snapshot : snapshots) {
            if (snapshot.getSoldCount() == null) continue;

            if (prevSales != null) {
                int delta = snapshot.getSoldCount() - prevSales;
                int threshold = Math.max(100, Math.abs(prevSales));
                if (Math.abs(delta) > threshold) {
                    abnormalCount++;
                }
            }
            prevSales = snapshot.getSoldCount();
        }

        double abnormalRatio = (double) abnormalCount / snapshots.size();
        return (int) (100 * (1 - abnormalRatio));
    }

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
        if (snapshot7dAgo != null && currentSnapshot.getSoldCount() != null && snapshot7dAgo.getSoldCount() != null) {
            int salesDelta7d = currentSnapshot.getSoldCount() - snapshot7dAgo.getSoldCount();
            if (snapshot7dAgo.getSoldCount() > 0) {
                double trend7d = (double) salesDelta7d / snapshot7dAgo.getSoldCount() * 100;
                signal.setTrend7d(BigDecimal.valueOf(trend7d).setScale(2, RoundingMode.HALF_UP));
            }

            // 评论增长速度（每日）
            signal.setReviewVelocity(BigDecimal.valueOf(salesDelta7d / 7.0).setScale(2, RoundingMode.HALF_UP));
        }

        // 计算30天趋势
        if (snapshot30dAgo != null && currentSnapshot.getSoldCount() != null && snapshot30dAgo.getSoldCount() != null) {
            int salesDelta30d = currentSnapshot.getSoldCount() - snapshot30dAgo.getSoldCount();
            if (snapshot30dAgo.getSoldCount() > 0) {
                double trend30d = (double) salesDelta30d / snapshot30dAgo.getSoldCount() * 100;
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
