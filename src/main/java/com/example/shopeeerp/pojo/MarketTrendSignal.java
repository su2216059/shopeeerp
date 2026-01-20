package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 市场趋势信号
 */
public class MarketTrendSignal {
    private Long id;
    private String platform;
    private String platformProductId;
    private LocalDate signalDate;
    private BigDecimal trend7d;      // 7天增长率 %
    private BigDecimal trend30d;     // 30天增长率 %
    private Integer rankChange7d;    // 7天排名变化
    private BigDecimal reviewVelocity;  // 评论增长速度（每日）
    private String stockRiskLevel;   // low / medium / high
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformProductId() {
        return platformProductId;
    }

    public void setPlatformProductId(String platformProductId) {
        this.platformProductId = platformProductId;
    }

    public LocalDate getSignalDate() {
        return signalDate;
    }

    public void setSignalDate(LocalDate signalDate) {
        this.signalDate = signalDate;
    }

    public BigDecimal getTrend7d() {
        return trend7d;
    }

    public void setTrend7d(BigDecimal trend7d) {
        this.trend7d = trend7d;
    }

    public BigDecimal getTrend30d() {
        return trend30d;
    }

    public void setTrend30d(BigDecimal trend30d) {
        this.trend30d = trend30d;
    }

    public Integer getRankChange7d() {
        return rankChange7d;
    }

    public void setRankChange7d(Integer rankChange7d) {
        this.rankChange7d = rankChange7d;
    }

    public BigDecimal getReviewVelocity() {
        return reviewVelocity;
    }

    public void setReviewVelocity(BigDecimal reviewVelocity) {
        this.reviewVelocity = reviewVelocity;
    }

    public String getStockRiskLevel() {
        return stockRiskLevel;
    }

    public void setStockRiskLevel(String stockRiskLevel) {
        this.stockRiskLevel = stockRiskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
