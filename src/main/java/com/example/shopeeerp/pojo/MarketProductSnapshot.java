package com.example.shopeeerp.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MarketProductSnapshot {
    private Long id;
    private String platform;
    private String platformProductId;
    private LocalDate snapshotDate;
    private BigDecimal price;
    private BigDecimal rating;
    private Integer reviewCount;
    private String availabilityStatus;
    private String stockHint;
    private Integer categoryRank;
    private Integer searchRank;
    private String dataSource;
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

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getStockHint() {
        return stockHint;
    }

    public void setStockHint(String stockHint) {
        this.stockHint = stockHint;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public void setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
    }

    public Integer getSearchRank() {
        return searchRank;
    }

    public void setSearchRank(Integer searchRank) {
        this.searchRank = searchRank;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
