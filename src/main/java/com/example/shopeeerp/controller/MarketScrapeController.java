package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.MarketScrapeRecord;
import com.example.shopeeerp.service.MarketScrapeRecordService;
import com.example.shopeeerp.service.MarketSignalIngestService;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market/scrape")
@CrossOrigin(origins = "*")
public class MarketScrapeController {

    @Autowired
    private MarketScrapeRecordService recordService;
    @Autowired
    private MarketSignalIngestService ingestService;

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingest(@RequestBody(required = false) MarketScrapeIngestRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (request == null) {
            resp.put("success", false);
            resp.put("message", "payload is required");
            return ResponseEntity.badRequest().body(resp);
        }

        List<MarketSignalIngestService.MarketSnapshotIngestItem> items = new ArrayList<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (MarketScrapeIngestItem item : request.getItems()) {
                if (item != null) {
                    items.add(item.toIngestItem());
                }
            }
        } else if (request.getItem() != null) {
            items.add(request.getItem().toIngestItem());
        }

        if (items.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "items is required");
            return ResponseEntity.badRequest().body(resp);
        }

        MarketScrapeRecord record = buildRecord(request);
        recordService.save(record);

        MarketSignalIngestService.IngestResult result = ingestService.ingest(items);
        resp.put("success", true);
        resp.put("total", result.getTotal());
        resp.put("skipped", result.getSkipped());
        resp.put("productUpserts", result.getProductUpserts());
        resp.put("snapshotUpserts", result.getSnapshotUpserts());
        return ResponseEntity.ok(resp);
    }

    private MarketScrapeRecord buildRecord(MarketScrapeIngestRequest request) {
        MarketScrapeRecord record = new MarketScrapeRecord();
        record.setPlatform(request.getPlatform());
        record.setMarket(request.getMarket());
        record.setUrl(request.getUrl());
        record.setDataType(request.getDataType());
        record.setFetchedAt(parseDateTime(request.getFetchedAt()));
        record.setRawPayload(request.getRawPayload());
        record.setParsedPayload(request.getParsedPayload());
        record.setStatus("RECEIVED");
        record.setCreatedAt(LocalDateTime.now());
        return record;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static class MarketScrapeIngestRequest {
        private String platform;
        private String market;
        private String url;
        @JsonProperty("data_type")
        @JsonAlias("dataType")
        private String dataType;
        @JsonProperty("fetched_at")
        @JsonAlias("fetchedAt")
        private String fetchedAt;
        @JsonProperty("raw_payload")
        @JsonAlias("rawPayload")
        private String rawPayload;
        @JsonProperty("parsed_payload")
        @JsonAlias("parsedPayload")
        private String parsedPayload;
        private MarketScrapeIngestItem item;
        private List<MarketScrapeIngestItem> items;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getFetchedAt() {
            return fetchedAt;
        }

        public void setFetchedAt(String fetchedAt) {
            this.fetchedAt = fetchedAt;
        }

        public String getRawPayload() {
            return rawPayload;
        }

        public void setRawPayload(String rawPayload) {
            this.rawPayload = rawPayload;
        }

        public String getParsedPayload() {
            return parsedPayload;
        }

        public void setParsedPayload(String parsedPayload) {
            this.parsedPayload = parsedPayload;
        }

        public MarketScrapeIngestItem getItem() {
            return item;
        }

        public void setItem(MarketScrapeIngestItem item) {
            this.item = item;
        }

        public List<MarketScrapeIngestItem> getItems() {
            return items;
        }

        public void setItems(List<MarketScrapeIngestItem> items) {
            this.items = items;
        }
    }

    public static class MarketScrapeIngestItem {
        private String platform;
        private String market;
        @JsonProperty("platform_product_id")
        @JsonAlias("platformProductId")
        private String platformProductId;
        @JsonProperty("platform_sku_id")
        @JsonAlias("platformSkuId")
        private String platformSkuId;
        private String title;
        private String brand;
        @JsonProperty("category_id")
        @JsonAlias("categoryId")
        private String categoryId;
        @JsonProperty("category_path")
        @JsonAlias("categoryPath")
        private String categoryPath;
        @JsonProperty("snapshot_date")
        @JsonAlias("snapshotDate")
        private String snapshotDate;
        private java.math.BigDecimal price;
        private java.math.BigDecimal rating;
        @JsonProperty("review_count")
        @JsonAlias("reviewCount")
        private Integer reviewCount;
        @JsonProperty("availability_status")
        @JsonAlias("availabilityStatus")
        private String availabilityStatus;
        @JsonProperty("stock_hint")
        @JsonAlias("stockHint")
        private String stockHint;
        @JsonProperty("category_rank")
        @JsonAlias("categoryRank")
        private Integer categoryRank;
        @JsonProperty("search_rank")
        @JsonAlias("searchRank")
        private Integer searchRank;
        @JsonProperty("data_source")
        @JsonAlias("dataSource")
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

        public MarketSignalIngestService.MarketSnapshotIngestItem toIngestItem() {
            MarketSignalIngestService.MarketSnapshotIngestItem item =
                    new MarketSignalIngestService.MarketSnapshotIngestItem();
            item.setPlatform(platform);
            item.setMarket(market);
            item.setPlatformProductId(platformProductId);
            item.setPlatformSkuId(platformSkuId);
            item.setTitle(title);
            item.setBrand(brand);
            item.setCategoryId(categoryId);
            item.setCategoryPath(categoryPath);
            item.setSnapshotDate(snapshotDate);
            item.setPrice(price);
            item.setRating(rating);
            item.setReviewCount(reviewCount);
            item.setAvailabilityStatus(availabilityStatus);
            item.setStockHint(stockHint);
            item.setCategoryRank(categoryRank);
            item.setSearchRank(searchRank);
            item.setDataSource(dataSource);
            return item;
        }
    }
}
