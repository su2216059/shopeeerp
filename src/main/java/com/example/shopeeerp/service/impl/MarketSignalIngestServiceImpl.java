package com.example.shopeeerp.service.impl;

import com.example.shopeeerp.mapper.MarketProductMapper;
import com.example.shopeeerp.mapper.MarketProductSnapshotMapper;
import com.example.shopeeerp.pojo.MarketProduct;
import com.example.shopeeerp.pojo.MarketProductSnapshot;
import com.example.shopeeerp.service.MarketSignalIngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class MarketSignalIngestServiceImpl implements MarketSignalIngestService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MarketProductMapper marketProductMapper;
    @Autowired
    private MarketProductSnapshotMapper marketProductSnapshotMapper;

    @Override
    @Transactional
    public IngestResult ingest(List<MarketSnapshotIngestItem> items) {
        IngestResult result = new IngestResult();
        if (items == null || items.isEmpty()) {
            return result;
        }

        int skipped = 0;
        int productUpserts = 0;
        int snapshotUpserts = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        for (MarketSnapshotIngestItem item : items) {
            if (item == null || isBlank(item.getPlatformProductId())) {
                skipped++;
                continue;
            }
            String platform = normalizePlatform(item.getPlatform());
            String market = normalizeMarket(item.getMarket());

            MarketProduct product = new MarketProduct();
            product.setPlatform(platform);
            product.setMarket(market);
            product.setPlatformProductId(item.getPlatformProductId());
            product.setPlatformSkuId(item.getPlatformSkuId());
            product.setTitle(item.getTitle());
            product.setBrand(item.getBrand());
            product.setCategoryId(item.getCategoryId());
            product.setCategoryPath(item.getCategoryPath());
            LocalDate listedDate = parseDateSafe(item.getListedAt());

            // If listedAt is provided, use it as firstSeenAt.
            LocalDateTime firstSeenAt = now;
            if (listedDate != null) {
                firstSeenAt = listedDate.atStartOfDay();
            }
            product.setListedAt(listedDate);
            product.setFirstSeenAt(firstSeenAt);
            product.setLastSeenAt(now);
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            productUpserts += marketProductMapper.upsert(product);

            MarketProductSnapshot snapshot = new MarketProductSnapshot();
            snapshot.setPlatform(platform);
            snapshot.setPlatformProductId(item.getPlatformProductId());
            LocalDate snapshotDate = parseDate(item.getSnapshotDate(), today);
            LocalDate salesUpdateDate = parseDateSafe(item.getSalesUpdateAt());
            if (isBlank(item.getSnapshotDate()) && salesUpdateDate != null) {
                snapshotDate = salesUpdateDate;
            }
            snapshot.setSnapshotDate(snapshotDate);
            snapshot.setPrice(item.getPrice() != null ? item.getPrice() : item.getAvgPrice());
            snapshot.setRating(item.getRating());
            snapshot.setReviewCount(item.getReviewCount());
            snapshot.setSoldCount(parseInt(item.getSoldCount()));
            snapshot.setSoldSum(item.getSoldSum());
            snapshot.setGmvSum(item.getGmvSum());
            snapshot.setAvgPrice(item.getAvgPrice());
            snapshot.setAvgGmv(item.getAvgGmv());
            snapshot.setViews(parseInt(item.getViews()));
            snapshot.setSessionCount(parseInt(item.getSessionCount()));
            snapshot.setConvToCart(item.getConvToCart());
            snapshot.setConvViewToOrder(item.getConvViewToOrder());
            snapshot.setStock(parseInt(item.getStock()));
            snapshot.setFboStock(item.getFboStock());
            snapshot.setFbsStock(item.getFbsStock());
            snapshot.setCbStock(item.getCbStock());
            snapshot.setRetailStock(item.getRetailStock());
            snapshot.setSalesDynamics(item.getSalesDynamics());
            snapshot.setMinSellerPrice(item.getMinSellerPrice());
            snapshot.setSalesPeriod(normalizePeriod(item.getSalesPeriod()));
            snapshot.setSalesUpdateAt(parseDateTimeSafe(item.getSalesUpdateAt()));
            snapshot.setItemPayloadJson(item.getItemPayloadJson());
            snapshot.setBenchmarkJson(item.getBenchmarkJson());
            snapshot.setAvailabilityStatus(item.getAvailabilityStatus());
            snapshot.setStockHint(item.getStockHint());
            snapshot.setCategoryRank(item.getCategoryRank());
            snapshot.setSearchRank(item.getSearchRank());
            snapshot.setDataSource(item.getDataSource());
            snapshot.setCreatedAt(now);
            snapshotUpserts += marketProductSnapshotMapper.upsert(snapshot);
        }

        result.setTotal(items.size());
        result.setSkipped(skipped);
        result.setProductUpserts(productUpserts);
        result.setSnapshotUpserts(snapshotUpserts);
        return result;
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (isBlank(value)) {
            return fallback;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FMT);
        } catch (Exception e) {
            return fallback;
        }
    }

    private LocalDate parseDateSafe(String value) {
        if (isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return LocalDate.parse(trimmed, DATE_FMT);
        } catch (DateTimeParseException e) {
            // continue
        }
        if (trimmed.length() >= 10) {
            try {
                return LocalDate.parse(trimmed.substring(0, 10), DATE_FMT);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }

    private LocalDateTime parseDateTimeSafe(String value) {
        if (isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            // continue
        }
        try {
            return OffsetDateTime.parse(trimmed).toLocalDateTime();
        } catch (DateTimeParseException e) {
            // continue
        }
        try {
            return LocalDateTime.parse(trimmed, DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            // continue
        }
        if (trimmed.length() >= 10) {
            LocalDate date = parseDateSafe(trimmed);
            return date != null ? date.atStartOfDay() : null;
        }
        return null;
    }

    private Integer parseInt(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            try {
                return new java.math.BigDecimal(value.trim()).intValue();
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    private String normalizePeriod(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private String normalizePlatform(String value) {
        if (isBlank(value)) {
            return "ozon";
        }
        return value.trim().toLowerCase();
    }

    private String normalizeMarket(String value) {
        if (isBlank(value)) {
            return "RU";
        }
        return value.trim().toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
