package com.example.shopeeerp.service;

import java.util.List;

public interface MarketSignalIngestService {

    IngestResult ingest(List<MarketSnapshotIngestItem> items);

    class IngestResult {
        private int total;
        private int skipped;
        private int productUpserts;
        private int snapshotUpserts;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSkipped() {
            return skipped;
        }

        public void setSkipped(int skipped) {
            this.skipped = skipped;
        }

        public int getProductUpserts() {
            return productUpserts;
        }

        public void setProductUpserts(int productUpserts) {
            this.productUpserts = productUpserts;
        }

        public int getSnapshotUpserts() {
            return snapshotUpserts;
        }

        public void setSnapshotUpserts(int snapshotUpserts) {
            this.snapshotUpserts = snapshotUpserts;
        }
    }

    class MarketSnapshotIngestItem {
        private String platform;
        private String market;
        private String platformProductId;
        private String platformSkuId;
        private String title;
        private String brand;
        private String categoryId;
        private String categoryPath;
        private String snapshotDate;
        private java.math.BigDecimal price;
        private java.math.BigDecimal rating;
        private Integer reviewCount;
        private String soldCount;
        private java.math.BigDecimal soldSum;
        private java.math.BigDecimal gmvSum;
        private java.math.BigDecimal avgPrice;
        private java.math.BigDecimal avgGmv;
        private String views;
        private String sessionCount;
        private java.math.BigDecimal convToCart;
        private java.math.BigDecimal convViewToOrder;
        private String stock;
        private Integer fboStock;
        private Integer fbsStock;
        private Integer cbStock;
        private Integer retailStock;
        private java.math.BigDecimal salesDynamics;
        private java.math.BigDecimal minSellerPrice;
        private String salesPeriod;
        private String salesUpdateAt;
        private String itemPayloadJson;
        private String benchmarkJson;
        private String availabilityStatus;
        private String stockHint;
        private Integer categoryRank;
        private Integer searchRank;
        private String dataSource;
        private String listedAt;  // 商品上架时间

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getPlatformProductId() {
            return platformProductId;
        }

        public void setPlatformProductId(String platformProductId) {
            this.platformProductId = platformProductId;
        }

        public String getPlatformSkuId() {
            return platformSkuId;
        }

        public void setPlatformSkuId(String platformSkuId) {
            this.platformSkuId = platformSkuId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryPath() {
            return categoryPath;
        }

        public void setCategoryPath(String categoryPath) {
            this.categoryPath = categoryPath;
        }

        public String getSnapshotDate() {
            return snapshotDate;
        }

        public void setSnapshotDate(String snapshotDate) {
            this.snapshotDate = snapshotDate;
        }

        public java.math.BigDecimal getPrice() {
            return price;
        }

        public void setPrice(java.math.BigDecimal price) {
            this.price = price;
        }

        public java.math.BigDecimal getRating() {
            return rating;
        }

        public void setRating(java.math.BigDecimal rating) {
            this.rating = rating;
        }

        public Integer getReviewCount() {
            return reviewCount;
        }

        public void setReviewCount(Integer reviewCount) {
            this.reviewCount = reviewCount;
        }

        public String getSoldCount() {
            return soldCount;
        }

        public void setSoldCount(String soldCount) {
            this.soldCount = soldCount;
        }

        public java.math.BigDecimal getSoldSum() {
            return soldSum;
        }

        public void setSoldSum(java.math.BigDecimal soldSum) {
            this.soldSum = soldSum;
        }

        public java.math.BigDecimal getGmvSum() {
            return gmvSum;
        }

        public void setGmvSum(java.math.BigDecimal gmvSum) {
            this.gmvSum = gmvSum;
        }

        public java.math.BigDecimal getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(java.math.BigDecimal avgPrice) {
            this.avgPrice = avgPrice;
        }

        public java.math.BigDecimal getAvgGmv() {
            return avgGmv;
        }

        public void setAvgGmv(java.math.BigDecimal avgGmv) {
            this.avgGmv = avgGmv;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getSessionCount() {
            return sessionCount;
        }

        public void setSessionCount(String sessionCount) {
            this.sessionCount = sessionCount;
        }

        public java.math.BigDecimal getConvToCart() {
            return convToCart;
        }

        public void setConvToCart(java.math.BigDecimal convToCart) {
            this.convToCart = convToCart;
        }

        public java.math.BigDecimal getConvViewToOrder() {
            return convViewToOrder;
        }

        public void setConvViewToOrder(java.math.BigDecimal convViewToOrder) {
            this.convViewToOrder = convViewToOrder;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
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

        public java.math.BigDecimal getSalesDynamics() {
            return salesDynamics;
        }

        public void setSalesDynamics(java.math.BigDecimal salesDynamics) {
            this.salesDynamics = salesDynamics;
        }

        public java.math.BigDecimal getMinSellerPrice() {
            return minSellerPrice;
        }

        public void setMinSellerPrice(java.math.BigDecimal minSellerPrice) {
            this.minSellerPrice = minSellerPrice;
        }

        public String getSalesPeriod() {
            return salesPeriod;
        }

        public void setSalesPeriod(String salesPeriod) {
            this.salesPeriod = salesPeriod;
        }

        public String getSalesUpdateAt() {
            return salesUpdateAt;
        }

        public void setSalesUpdateAt(String salesUpdateAt) {
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

        public String getListedAt() {
            return listedAt;
        }

        public void setListedAt(String listedAt) {
            this.listedAt = listedAt;
        }
    }
}
