package com.example.shopeeerp.controller;

import com.example.shopeeerp.mapper.MarketProductMapper;
import com.example.shopeeerp.mapper.MarketProductSnapshotMapper;
import com.example.shopeeerp.mapper.MarketSalesEstimateMapper;
import com.example.shopeeerp.mapper.MarketTrendSignalMapper;
import com.example.shopeeerp.pojo.MarketProduct;
import com.example.shopeeerp.pojo.MarketProductSnapshot;
import com.example.shopeeerp.pojo.MarketSalesEstimate;
import com.example.shopeeerp.pojo.MarketTrendSignal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 市场商品 API
 */
@RestController
@RequestMapping("/market/products")
@CrossOrigin(origins = "*")
public class MarketProductController {

    @Autowired
    private MarketProductMapper productMapper;

    @Autowired
    private MarketProductSnapshotMapper snapshotMapper;

    @Autowired
    private MarketSalesEstimateMapper estimateMapper;

    @Autowired
    private MarketTrendSignalMapper trendMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询商品列表
     * GET /market/products?platform=ozon&keyword=xxx&brand=xxx&page=1&size=20
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "ozon") String platform,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        int offset = (page - 1) * size;
        List<MarketProduct> products = productMapper.selectList(platform, keyword, brand, categoryId, offset, size);
        int total = productMapper.countList(platform, keyword, brand, categoryId);

        Map<String, Object> result = new HashMap<>();
        result.put("data", products);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (total + size - 1) / size);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取商品详情（包含最新快照、销量估算、趋势信号）
     * GET /market/products/{platform}/{productId}
     */
    @GetMapping("/{platform}/{productId}")
    public ResponseEntity<?> getDetail(
            @PathVariable String platform,
            @PathVariable String productId) {

        MarketProduct product = productMapper.selectByPlatformAndProductId(platform, productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        // 获取最新快照
        MarketProductSnapshot latestSnapshot = snapshotMapper.selectLatestByProduct(platform, productId);

        // 获取最新销量估算
        MarketSalesEstimate weeklyEstimate = estimateMapper.selectLatestByProduct(platform, productId, "weekly");
        MarketSalesEstimate monthlyEstimate = estimateMapper.selectLatestByProduct(platform, productId, "monthly");

        // 获取最新趋势信号
        MarketTrendSignal trendSignal = trendMapper.selectLatestByProduct(platform, productId);

        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("latestSnapshot", latestSnapshot);
        result.put("weeklyEstimate", weeklyEstimate);
        result.put("monthlyEstimate", monthlyEstimate);
        result.put("trendSignal", trendSignal);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取商品快照历史
     * GET /market/products/{platform}/{productId}/snapshots?limit=30
     */
    @GetMapping("/{platform}/{productId}/snapshots")
    public ResponseEntity<?> getSnapshots(
            @PathVariable String platform,
            @PathVariable String productId,
            @RequestParam(defaultValue = "30") int limit) {

        java.time.LocalDate endDate = java.time.LocalDate.now();
        java.time.LocalDate startDate = endDate.minusDays(limit);

        List<MarketProductSnapshot> snapshots = snapshotMapper.selectByProductAndDateRange(
                platform, productId, startDate, endDate);

        return ResponseEntity.ok(snapshots);
    }

    /**
     * 获取筛选选项（品牌、分类）
     * GET /market/products/filters?platform=ozon
     */
    @GetMapping("/filters")
    public ResponseEntity<?> getFilters(@RequestParam(defaultValue = "ozon") String platform) {
        List<String> brands = productMapper.selectDistinctBrands(platform);
        List<String> categories = productMapper.selectDistinctCategories(platform);

        Map<String, Object> result = new HashMap<>();
        result.put("brands", brands);
        result.put("categories", categories);

        return ResponseEntity.ok(result);
    }

    /**
     * 获取热门商品榜单
     * GET /market/products/trending?platform=ozon&type=trend7d&limit=20
     */
    @GetMapping("/trending")
    public ResponseEntity<?> getTrending(
            @RequestParam(defaultValue = "ozon") String platform,
            @RequestParam(defaultValue = "trend7d") String type,
            @RequestParam(defaultValue = "20") int limit) {

        java.time.LocalDate today = java.time.LocalDate.now();
        // 尝试最近7天内有数据的日期
        java.time.LocalDate signalDate = today;
        List<MarketTrendSignal> signals = null;

        for (int i = 0; i < 7; i++) {
            signalDate = today.minusDays(i);
            switch (type) {
                case "trend7d":
                    signals = trendMapper.selectTopTrending(platform, signalDate, limit);
                    break;
                case "rankRising":
                    signals = trendMapper.selectTopRankRising(platform, signalDate, limit);
                    break;
                case "reviewVelocity":
                    signals = trendMapper.selectTopReviewVelocity(platform, signalDate, limit);
                    break;
                default:
                    signals = trendMapper.selectTopTrending(platform, signalDate, limit);
            }
            if (signals != null && !signals.isEmpty()) {
                break;
            }
        }

        // 获取商品详情
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        if (signals != null) {
            for (MarketTrendSignal signal : signals) {
                Map<String, Object> item = new HashMap<>();
                item.put("signal", signal);
                MarketProduct product = productMapper.selectByPlatformAndProductId(platform, signal.getPlatformProductId());
                item.put("product", product);
                MarketProductSnapshot snapshot = snapshotMapper.selectLatestByProduct(platform, signal.getPlatformProductId());
                item.put("latestSnapshot", snapshot);
                result.add(item);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        response.put("signalDate", signalDate.toString());
        response.put("type", type);

        return ResponseEntity.ok(response);
    }

    /**
     * Ozon销量数据代理接口
     * POST /market/products/ozon-sales-proxy
     */
    @PostMapping("/ozon-sales-proxy")
    public ResponseEntity<?> ozonSalesProxy(@RequestBody Map<String, Object> requestBody) {
        String url = "https://seller.ozon.ru/api/site/seller-analytics/what_to_sell/data/v3";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
