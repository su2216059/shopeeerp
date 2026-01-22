package com.example.shopeeerp.controller;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market")
@CrossOrigin(origins = "*")
public class MarketSignalController {

    @Autowired
    private MarketSignalIngestService ingestService;

    @PostMapping("/snapshots/ingest")
    public ResponseEntity<Map<String, Object>> ingestSnapshots(
            @RequestBody(required = false) List<MarketSnapshotIngestRequest> payload) {
        Map<String, Object> resp = new HashMap<>();
        if (payload == null) {
            resp.put("success", false);
            resp.put("message", "payload is required");
            return ResponseEntity.badRequest().body(resp);
        }
        List<MarketSignalIngestService.MarketSnapshotIngestItem> items = new ArrayList<>();
        for (MarketSnapshotIngestRequest request : payload) {
            if (request == null) {
                continue;
            }
            items.add(request.toItem());
        }
        MarketSignalIngestService.IngestResult result = ingestService.ingest(items);
        resp.put("success", true);
        resp.put("total", result.getTotal());
        resp.put("skipped", result.getSkipped());
        resp.put("productUpserts", result.getProductUpserts());
        resp.put("snapshotUpserts", result.getSnapshotUpserts());
        return ResponseEntity.ok(resp);
    }

    public static class MarketSnapshotIngestRequest {
        private String platform;
        private String market;
        @JsonProperty("platform_product_id")
        @JsonAlias({"platformProductId", "sku"})
        private String platformProductId;
        @JsonProperty("platform_sku_id")
        @JsonAlias({"platformSkuId", "variantId"})
        private String platformSkuId;
        private String title;
        private String brand;
        @JsonProperty("category_id")
        @JsonAlias({"categoryId", "category3Id", "category2Id", "category1Id"})
        private String categoryId;
        @JsonProperty("category_path")
        @JsonAlias({"categoryPath", "category3", "category1"})
        private String categoryPath;
        @JsonProperty("snapshot_date")
        @JsonAlias("snapshotDate")
        private String snapshotDate;
        private java.math.BigDecimal price;
        private java.math.BigDecimal rating;
        @JsonProperty("review_count")
        @JsonAlias("reviewCount")
        private Integer reviewCount;
        @JsonProperty("sold_count")
        @JsonAlias("soldCount")
        private String soldCount;
        @JsonProperty("sold_sum")
        @JsonAlias("soldSum")
        private java.math.BigDecimal soldSum;
        @JsonProperty("gmv_sum")
        @JsonAlias("gmvSum")
        private java.math.BigDecimal gmvSum;
        @JsonProperty("avg_price")
        @JsonAlias("avgPrice")
        private java.math.BigDecimal avgPrice;
        @JsonProperty("avg_gmv")
        @JsonAlias("avgGmv")
        private java.math.BigDecimal avgGmv;
        private String views;
        @JsonProperty("session_count")
        @JsonAlias("sessionCount")
        private String sessionCount;
        @JsonProperty("conv_to_cart")
        @JsonAlias("convToCart")
        private java.math.BigDecimal convToCart;
        @JsonProperty("conv_view_to_order")
        @JsonAlias("convViewToOrder")
        private java.math.BigDecimal convViewToOrder;
        private String stock;
        @JsonProperty("fbo_stock")
        @JsonAlias("fboStock")
        private Integer fboStock;
        @JsonProperty("fbs_stock")
        @JsonAlias("fbsStock")
        private Integer fbsStock;
        @JsonProperty("cb_stock")
        @JsonAlias("cbStock")
        private Integer cbStock;
        @JsonProperty("retail_stock")
        @JsonAlias("retailStock")
        private Integer retailStock;
        @JsonProperty("sales_dynamics")
        @JsonAlias("salesDynamics")
        private java.math.BigDecimal salesDynamics;
        @JsonProperty("min_seller_price")
        @JsonAlias("minSellerPrice")
        private java.math.BigDecimal minSellerPrice;
        @JsonProperty("sales_period")
        @JsonAlias("salesPeriod")
        private String salesPeriod;
        @JsonProperty("sales_update_at")
        @JsonAlias("salesUpdateAt")
        private String salesUpdateAt;
        @JsonProperty("item_payload_json")
        @JsonAlias("itemPayloadJson")
        private String itemPayloadJson;
        @JsonProperty("benchmark_json")
        @JsonAlias("benchmarkJson")
        private String benchmarkJson;
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
        @JsonProperty("listed_at")
        @JsonAlias({"listedAt", "nullableCreateDate"})
        private String listedAt;

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

        public MarketSignalIngestService.MarketSnapshotIngestItem toItem() {
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
            item.setSoldCount(soldCount);
            item.setSoldSum(soldSum);
            item.setGmvSum(gmvSum);
            item.setAvgPrice(avgPrice);
            item.setAvgGmv(avgGmv);
            item.setViews(views);
            item.setSessionCount(sessionCount);
            item.setConvToCart(convToCart);
            item.setConvViewToOrder(convViewToOrder);
            item.setStock(stock);
            item.setFboStock(fboStock);
            item.setFbsStock(fbsStock);
            item.setCbStock(cbStock);
            item.setRetailStock(retailStock);
            item.setSalesDynamics(salesDynamics);
            item.setMinSellerPrice(minSellerPrice);
            item.setSalesPeriod(salesPeriod);
            item.setSalesUpdateAt(salesUpdateAt);
            item.setItemPayloadJson(itemPayloadJson);
            item.setBenchmarkJson(benchmarkJson);
            item.setAvailabilityStatus(availabilityStatus);
            item.setStockHint(stockHint);
            item.setCategoryRank(categoryRank);
            item.setSearchRank(searchRank);
            item.setDataSource(dataSource);
            item.setListedAt(listedAt);
            return item;
        }
    }
}
