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
        private String availabilityStatus;
        private String stockHint;
        private Integer categoryRank;
        private Integer searchRank;
        private String dataSource;

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
    }
}
