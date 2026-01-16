package com.example.shopeeerp.controller;

import com.example.shopeeerp.pojo.OzonProduct;
import com.example.shopeeerp.pojo.OzonProductImage;
import com.example.shopeeerp.pojo.OzonProductStatus;
import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.PlatformAdapterFactory;
import com.example.shopeeerp.adapter.model.PlatformProduct;
import com.example.shopeeerp.service.OzonProductImageService;
import com.example.shopeeerp.service.OzonProductService;
import com.example.shopeeerp.service.OzonProductStatusService;
import com.example.shopeeerp.service.OzonProductStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Ozon 商品列表查询接口，用于前端展示同步数据。
 */
@RestController
@RequestMapping("/ozon/products")
@CrossOrigin(origins = "*")
public class OzonProductController {

    private final OzonProductService ozonProductService;
    private final OzonProductImageService ozonProductImageService;
    private final OzonProductStockService ozonProductStockService;
    private final OzonProductStatusService ozonProductStatusService;
    private final PlatformAdapterFactory platformAdapterFactory;
    private final AtomicBoolean syncing = new AtomicBoolean(false);
    private final Executor syncExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public OzonProductController(OzonProductService ozonProductService,
                                 OzonProductImageService ozonProductImageService,
                                 OzonProductStockService ozonProductStockService,
                                 OzonProductStatusService ozonProductStatusService,
                                 PlatformAdapterFactory platformAdapterFactory) {
        this.ozonProductService = ozonProductService;
        this.ozonProductImageService = ozonProductImageService;
        this.ozonProductStockService = ozonProductStockService;
        this.ozonProductStatusService = ozonProductStatusService;
        this.platformAdapterFactory = platformAdapterFactory;
    }

    @GetMapping
    public ResponseEntity<List<OzonProductView>> list(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "product_code", required = false) String productCode,
            @RequestParam(value = "created_from", required = false) String createdFrom,
            @RequestParam(value = "created_to", required = false) String createdTo,
            @RequestParam(value = "visibility", required = false) String visibility) {
        LocalDateTime from = parseDateTime(createdFrom);
        LocalDateTime to = parseDateTime(createdTo);
        String titleFilter = normalizeFilter(title);
        String codeFilter = normalizeFilter(productCode);
        String visibilityFilter = normalizeVisibilityFilter(visibility);
        List<OzonProduct> products = ozonProductService.getByFilters(
                titleFilter,
                codeFilter,
                from,
                to,
                visibilityFilter
        );
        List<OzonProductView> result = products.stream()
                .map(this::buildView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 触发 Ozon 商品同步并返回同步条数
     */
    @GetMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncProducts(
            @RequestParam(value = "visibility", required = false) String visibility) {
        Map<String, Object> result = new HashMap<>();
        if (!syncing.compareAndSet(false, true)) {
            result.put("success", false);
            result.put("message", "同步进行中，请稍后再试");
            return ResponseEntity.status(429).body(result);
        }

        try {
            PlatformAdapter adapter = platformAdapterFactory.getAdapter("ozon");
            CompletableFuture.runAsync(() -> {
                try {
                    if (adapter instanceof com.example.shopeeerp.adapter.impl.OzonAdapter) {
                        ((com.example.shopeeerp.adapter.impl.OzonAdapter) adapter).fetchProducts(visibility);
                    } else {
                        adapter.fetchProducts();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    syncing.set(false);
                }
            }, syncExecutor);

            result.put("success", true);
            result.put("message", "同步任务已启动，请稍后刷新列表");
            return ResponseEntity.accepted().body(result);
        } catch (IllegalArgumentException e) {
            syncing.set(false);
            result.put("success", false);
            result.put("message", "Platform not found: ozon");
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            syncing.set(false);
            result.put("success", false);
            result.put("message", "Sync failed to start");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    private OzonProductView buildView(OzonProduct product) {
        OzonProductView view = new OzonProductView();
        OzonProductStatus status = resolveStatusEntity(product.getId());
        view.setId(product.getId());
        view.setImageUrl(resolvePrimaryImage(product.getId()));
        view.setTitle(product.getName());
        view.setStore("Ozon");
        view.setProductCode(product.getOfferId());
        // 未提供变体数据源，暂为空
        view.setVariantCount(null);
        view.setStock(sumStock(product.getId()));
        view.setColor("");
        view.setSchedule("");
        view.setModel("");
        view.setWeight(product.getVolumeWeight());
        view.setLength(null);
        view.setWidth(null);
        view.setHeight(null);
        view.setPrice(product.getPrice());
        view.setMinPrice(parseDecimal(product.getMinPrice()));
        view.setOldPrice(product.getOldPrice());
        view.setStatus(resolveStatusLabel(status, product.getIsArchived()));
        view.setStatusCode(status != null ? status.getStatus() : null);
        view.setModerateStatus(status != null ? status.getModerateStatus() : null);
        view.setArchived(Boolean.TRUE.equals(product.getIsArchived()));
        view.setAutoArchived(Boolean.TRUE.equals(product.getIsAutoarchived()));
        view.setCreatedAt(product.getCreatedAt());
        view.setUpdatedAt(product.getUpdatedAt());
        return view;
    }

    private String resolvePrimaryImage(Long productId) {
        List<OzonProductImage> images = ozonProductImageService.getByProductId(productId);
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .sorted(Comparator
                        .comparing(OzonProductImage::getIsPrimary, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(OzonProductImage::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(OzonProductImage::getId))
                .map(OzonProductImage::getImageUrl)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Integer sumStock(Long productId) {
        try {
            return ozonProductStockService.sumPresentByProductId(productId);
        } catch (Exception e) {
            return 0;
        }
    }

    private OzonProductStatus resolveStatusEntity(Long productId) {
        if (ozonProductStatusService == null || productId == null) {
            return null;
        }
        try {
            return ozonProductStatusService.getByProductId(productId);
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveStatusLabel(OzonProductStatus status, Boolean isArchived) {
        if (status != null) {
            if (status.getStatusName() != null && !status.getStatusName().isEmpty()) {
                return status.getStatusName();
            }
            if (status.getStatus() != null && !status.getStatus().isEmpty()) {
                return status.getStatus();
            }
        }
        if (Boolean.TRUE.equals(isArchived)) {
            return "Archived";
        }
        return "";
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeFilter(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }

    private String normalizeVisibilityFilter(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private boolean matchesText(String value, String filter) {
        if (filter == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains(filter);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return java.time.OffsetDateTime.parse(value.trim(), java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(value.trim(), java.time.format.DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean matchesCreatedTime(OzonProduct product, LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return true;
        }
        if (product == null || product.getCreatedAt() == null) {
            return false;
        }
        LocalDateTime createdAt = product.getCreatedAt();
        if (from != null && createdAt.isBefore(from)) {
            return false;
        }
        if (to != null && createdAt.isAfter(to)) {
            return false;
        }
        return true;
    }

    private boolean matchesVisibility(OzonProductView view, String visibility) {
        if (visibility == null || visibility.isEmpty()) {
            return true;
        }
        if (view == null) {
            return false;
        }
        boolean archived = Boolean.TRUE.equals(view.getArchived()) || Boolean.TRUE.equals(view.getAutoArchived());
        int stock = view.getStock() != null ? view.getStock() : 0;
        if ("ARCHIVED".equals(visibility)) {
            return archived;
        }
        if ("IN_SALE".equals(visibility)) {
            return stock > 0 && !archived && !isPriceSent(view);
        }
        if ("TO_SUPPLY".equals(visibility)) {
            return stock == 0 && !archived && !isPriceSent(view);
        }
        if ("EMPTY_STOCK".equals(visibility)) {
            return stock == 0;
        }
        return true;
    }

    private boolean isPriceSent(OzonProductView view) {
        if (view == null || view.getStatusCode() == null) {
            return false;
        }
        return "price_sent".equalsIgnoreCase(view.getStatusCode());
    }

    /**
     * 前端展示用视图模型。
     */
    public static class OzonProductView {
        private Long id;
        private String imageUrl;
        private String title;
        private String store;
        private String productCode;
        private Integer variantCount;
        private Integer stock;
        private String color;
        private String schedule;
        private String model;
        private BigDecimal weight;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
        private BigDecimal price;
        private BigDecimal minPrice;
        private BigDecimal oldPrice;
        private String status;
        private String statusCode;
        private String moderateStatus;
        private Boolean archived;
        private Boolean autoArchived;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public Integer getVariantCount() {
            return variantCount;
        }

        public void setVariantCount(Integer variantCount) {
            this.variantCount = variantCount;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public BigDecimal getLength() {
            return length;
        }

        public void setLength(BigDecimal length) {
            this.length = length;
        }

        public BigDecimal getWidth() {
            return width;
        }

        public void setWidth(BigDecimal width) {
            this.width = width;
        }

        public BigDecimal getHeight() {
            return height;
        }

        public void setHeight(BigDecimal height) {
            this.height = height;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
        }

        public BigDecimal getOldPrice() {
            return oldPrice;
        }

        public void setOldPrice(BigDecimal oldPrice) {
            this.oldPrice = oldPrice;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getModerateStatus() {
            return moderateStatus;
        }

        public void setModerateStatus(String moderateStatus) {
            this.moderateStatus = moderateStatus;
        }

        public Boolean getArchived() {
            return archived;
        }

        public void setArchived(Boolean archived) {
            this.archived = archived;
        }

        public Boolean getAutoArchived() {
            return autoArchived;
        }

        public void setAutoArchived(Boolean autoArchived) {
            this.autoArchived = autoArchived;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
