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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MarketSignalIngestServiceImpl implements MarketSignalIngestService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_DATE;

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
            product.setFirstSeenAt(now);
            product.setLastSeenAt(now);
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            productUpserts += marketProductMapper.upsert(product);

            MarketProductSnapshot snapshot = new MarketProductSnapshot();
            snapshot.setPlatform(platform);
            snapshot.setPlatformProductId(item.getPlatformProductId());
            snapshot.setSnapshotDate(parseDate(item.getSnapshotDate(), today));
            snapshot.setPrice(item.getPrice());
            snapshot.setRating(item.getRating());
            snapshot.setReviewCount(item.getReviewCount());
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
