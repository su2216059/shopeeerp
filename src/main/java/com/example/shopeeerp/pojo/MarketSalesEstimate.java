package com.example.shopeeerp.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品销量估算结果
 */
public class MarketSalesEstimate {
    private Long id;
    private String platform;
    private String platformProductId;
    private String periodType;  // daily / weekly / monthly
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer estimatedSalesMin;
    private Integer estimatedSalesMax;
    private Integer estimatedSalesMid;
    private String estimationModel;  // v1_review_rank
    private Integer confidenceScore;  // 0-100
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

    public Integer getEstimatedSalesMin() {
        return estimatedSalesMin;
    }

    public void setEstimatedSalesMin(Integer estimatedSalesMin) {
        this.estimatedSalesMin = estimatedSalesMin;
    }

    public Integer getEstimatedSalesMax() {
        return estimatedSalesMax;
    }

    public void setEstimatedSalesMax(Integer estimatedSalesMax) {
        this.estimatedSalesMax = estimatedSalesMax;
    }

    public Integer getEstimatedSalesMid() {
        return estimatedSalesMid;
    }

    public void setEstimatedSalesMid(Integer estimatedSalesMid) {
        this.estimatedSalesMid = estimatedSalesMid;
    }

    public String getEstimationModel() {
        return estimationModel;
    }

    public void setEstimationModel(String estimationModel) {
        this.estimationModel = estimationModel;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
