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
    private Integer soldCount;
    private Integer sold_sum;
    private BigDecimal soldSum;
    private BigDecimal gmvSum;
    private BigDecimal avgPrice;
    private BigDecimal avgGmv;
    private Integer views;
    private Integer sessionCount;
    private BigDecimal convToCart;
    private BigDecimal convViewToOrder;
    private Integer stock;
    private Integer fboStock;
    private Integer fbsStock;
    private Integer cbStock;
    private Integer retailStock;
    private BigDecimal salesDynamics;
    private BigDecimal minSellerPrice;
    private String salesPeriod;
    private LocalDateTime salesUpdateAt;
    private String itemPayloadJson;
    private String benchmarkJson;
    private String availabilityStatus;
    private String stockHint;
    private Integer categoryRank;
    private Integer searchRank;
    private String dataSource;
    private LocalDateTime createdAt;

    public Integer getSold_sum() {
        return sold_sum;
    }

    public void setSold_sum(Integer sold_sum) {
        this.sold_sum = sold_sum;
    }

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

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public BigDecimal getSoldSum() {
        return soldSum;
    }

    public void setSoldSum(BigDecimal soldSum) {
        this.soldSum = soldSum;
    }

    public BigDecimal getGmvSum() {
        return gmvSum;
    }

    public void setGmvSum(BigDecimal gmvSum) {
        this.gmvSum = gmvSum;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public BigDecimal getAvgGmv() {
        return avgGmv;
    }

    public void setAvgGmv(BigDecimal avgGmv) {
        this.avgGmv = avgGmv;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public BigDecimal getConvToCart() {
        return convToCart;
    }

    public void setConvToCart(BigDecimal convToCart) {
        this.convToCart = convToCart;
    }

    public BigDecimal getConvViewToOrder() {
        return convViewToOrder;
    }

    public void setConvViewToOrder(BigDecimal convViewToOrder) {
        this.convViewToOrder = convViewToOrder;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getFboStock() {
        return fboStock;
    }

    public void setFboStock(Integer fboStock) {
        this.fboStock = fboStock;
    }

    public Integer getFbsStock() {
        return fbsStock;
    }

    public void setFbsStock(Integer fbsStock) {
        this.fbsStock = fbsStock;
    }

    public Integer getCbStock() {
        return cbStock;
    }

    public void setCbStock(Integer cbStock) {
        this.cbStock = cbStock;
    }

    public Integer getRetailStock() {
        return retailStock;
    }

    public void setRetailStock(Integer retailStock) {
        this.retailStock = retailStock;
    }

    public BigDecimal getSalesDynamics() {
        return salesDynamics;
    }

    public void setSalesDynamics(BigDecimal salesDynamics) {
        this.salesDynamics = salesDynamics;
    }

    public BigDecimal getMinSellerPrice() {
        return minSellerPrice;
    }

    public void setMinSellerPrice(BigDecimal minSellerPrice) {
        this.minSellerPrice = minSellerPrice;
    }

    public String getSalesPeriod() {
        return salesPeriod;
    }

    public void setSalesPeriod(String salesPeriod) {
        this.salesPeriod = salesPeriod;
    }

    public LocalDateTime getSalesUpdateAt() {
        return salesUpdateAt;
    }

    public void setSalesUpdateAt(LocalDateTime salesUpdateAt) {
        this.salesUpdateAt = salesUpdateAt;
    }

    public String getItemPayloadJson() {
        return itemPayloadJson;
    }

    public void setItemPayloadJson(String itemPayloadJson) {
        this.itemPayloadJson = itemPayloadJson;
    }

    public String getBenchmarkJson() {
        return benchmarkJson;
    }

    public void setBenchmarkJson(String benchmarkJson) {
        this.benchmarkJson = benchmarkJson;
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
