package com.example.shopeeerp.service.cache;

import com.example.shopeeerp.mapper.OzonProductMapper;
import com.example.shopeeerp.pojo.SkuCostRow;
import com.example.shopeeerp.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PurchasePriceCacheService {
    private static final String NULL_SENTINEL = "NULL";
    private static final int DEFAULT_BATCH_SIZE = 500;

    private final StringRedisTemplate stringRedisTemplate;
    private final OzonProductMapper ozonProductMapper;
    private final boolean redisEnabled;
    private final long ttlMinutes;
    private final long nullTtlMinutes;
    private final long jitterSeconds;

    public PurchasePriceCacheService(
            StringRedisTemplate stringRedisTemplate,
            OzonProductMapper ozonProductMapper,
            @Value("${app.redis.enabled:true}") boolean redisEnabled,
            @Value("${app.cache.purchase-price.ttl-minutes:10}") long ttlMinutes,
            @Value("${app.cache.purchase-price.null-ttl-minutes:2}") long nullTtlMinutes,
            @Value("${app.cache.purchase-price.jitter-seconds:60}") long jitterSeconds) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.ozonProductMapper = ozonProductMapper;
        this.redisEnabled = redisEnabled;
        this.ttlMinutes = ttlMinutes;
        this.nullTtlMinutes = nullTtlMinutes;
        this.jitterSeconds = jitterSeconds;
    }

    /**
     * Batch get purchase prices by sku.
     * - Redis hit: return cached value (NULL -> 0)
     * - Redis miss: fallback to MySQL (batch) and backfill cache
     * - Redis unavailable: direct MySQL (batch), no cache write
     */
    public Map<String, BigDecimal> getPurchasePrices(Long shopId, List<Long> skus) {
        if (shopId == null || skus == null || skus.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> normalizedSkus = skus.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (normalizedSkus.isEmpty()) {
            return Collections.emptyMap();
        }

        if (!redisEnabled) {
            return loadFromMySql(shopId, normalizedSkus);
        }

        try {
            Map<String, BigDecimal> result = new HashMap<>();
            List<Long> misses = new ArrayList<>();

            for (int i = 0; i < normalizedSkus.size(); i += DEFAULT_BATCH_SIZE) {
                List<Long> batch = normalizedSkus.subList(i, Math.min(i + DEFAULT_BATCH_SIZE, normalizedSkus.size()));
                List<String> keys = batch.stream()
                        .map(sku -> RedisUtils.buildKey(shopId, String.valueOf(sku)))
                        .collect(Collectors.toList());

                List<String> cachedValues = stringRedisTemplate.opsForValue().multiGet(keys);
                if (cachedValues == null || cachedValues.size() != keys.size()) {
                    // Defensive fallback; treat as all misses.
                    misses.addAll(batch);
                    continue;
                }

                for (int idx = 0; idx < batch.size(); idx++) {
                    Long sku = batch.get(idx);
                    String cached = cachedValues.get(idx);
                    if (cached == null) {
                        misses.add(sku);
                        continue;
                    }
                    String skuKey = String.valueOf(sku);
                    if (NULL_SENTINEL.equals(cached)) {
                        result.put(skuKey, BigDecimal.ZERO);
                        continue;
                    }
                    try {
                        result.put(skuKey, new BigDecimal(cached));
                    } catch (Exception ex) {
                        // Bad cache value, treat as miss and overwrite later.
                        misses.add(sku);
                    }
                }
            }

            if (!misses.isEmpty()) {
                Map<String, BigDecimal> loaded = loadFromMySql(shopId, misses);
                result.putAll(loaded);
                backfillCache(shopId, misses, loaded);
            }

            return result;
        } catch (DataAccessException ex) {
            log.warn("redis unavailable, fallback to mysql: shopId={}, skus={}", shopId, normalizedSkus.size());
            return loadFromMySql(shopId, normalizedSkus);
        }
    }

    private Map<String, BigDecimal> loadFromMySql(Long shopId, List<Long> skus) {
        if (skus == null || skus.isEmpty()) {
            return Collections.emptyMap();
        }
        List<SkuCostRow> rows = ozonProductMapper.selectPurchasePrices(shopId, skus);
        Map<String, BigDecimal> result = new HashMap<>();
        if (rows != null) {
            for (SkuCostRow row : rows) {
                if (row == null || row.getSku() == null) {
                    continue;
                }
                BigDecimal price = row.getPurchasePrice() == null ? BigDecimal.ZERO : row.getPurchasePrice();
                result.put(String.valueOf(row.getSku()), price);
            }
        }
        // Note: missing sku -> treated as 0 by caller.
        return result;
    }

    private void backfillCache(Long shopId, List<Long> missSkus, Map<String, BigDecimal> loaded) {
        if (missSkus == null || missSkus.isEmpty()) {
            return;
        }
        Duration ttl = Duration.ofMinutes(Math.max(1, ttlMinutes));
        Duration nullTtl = Duration.ofMinutes(Math.max(1, nullTtlMinutes));
        int jitterBound = (int) Math.max(0, jitterSeconds);

        for (Long sku : missSkus) {
            if (sku == null) {
                continue;
            }
            String skuKey = String.valueOf(sku);
            String key = RedisUtils.buildKey(shopId, skuKey);

            BigDecimal price = loaded != null ? loaded.get(skuKey) : null;
            if (price == null) {
                // Cache NULL sentinel with short TTL.
                try {
                    stringRedisTemplate.opsForValue().set(key, NULL_SENTINEL, nullTtl);
                } catch (Exception ignored) {
                }
                continue;
            }

            long extraSeconds = (jitterBound <= 0) ? 0 : ThreadLocalRandom.current().nextInt(0, jitterBound + 1);
            try {
                stringRedisTemplate.opsForValue().set(key, price.toPlainString(), ttl.plusSeconds(extraSeconds));
            } catch (Exception ignored) {
            }
        }
    }
}
