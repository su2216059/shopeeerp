package com.example.shopeeerp.adapter.impl;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductInfoRequest;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductInfoResponse;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductListRequest;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductListResponse;
import com.example.shopeeerp.adapter.model.PlatformCost;
import com.example.shopeeerp.adapter.model.PlatformOrder;
import com.example.shopeeerp.adapter.model.PlatformProduct;
import com.example.shopeeerp.pojo.OzonProduct;
import com.example.shopeeerp.pojo.OzonProductStatus;
import com.example.shopeeerp.service.OzonProductImageService;
import com.example.shopeeerp.service.OzonProductService;
import com.example.shopeeerp.service.OzonProductStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ozon 平台适配器
 */
@Component
public class OzonAdapter implements PlatformAdapter {

    private static final String OZON_API_BASE_URL = "https://api-seller.ozon.ru";
    private static final String PRODUCT_LIST_URL = OZON_API_BASE_URL + "/v3/product/list";
    private static final String PRODUCT_INFO_URL = OZON_API_BASE_URL + "/v3/product/info/list";

    private final RestTemplate restTemplate;
    private final OzonProductService ozonProductService;
    private final OzonProductImageService ozonProductImageService;
    private final OzonProductStatusService ozonProductStatusService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ozon.api.client-id:}")
    private String clientId;

    @Value("${ozon.api.api-key:}")
    private String apiKey;

    @Autowired
    public OzonAdapter(RestTemplate restTemplate,
                       @Autowired(required = false) OzonProductService ozonProductService,
                       @Autowired(required = false) OzonProductImageService ozonProductImageService,
                       @Autowired(required = false) OzonProductStatusService ozonProductStatusService) {
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
        this.ozonProductService = ozonProductService;
        this.ozonProductImageService = ozonProductImageService;
        this.ozonProductStatusService = ozonProductStatusService;
    }

    @Override
    public String getPlatformName() {
        return "Ozon";
    }

    @Override
    public List<PlatformOrder> fetchOrders(String startDate, String endDate) {
        // TODO 调用 Ozon 订单 API
        List<PlatformOrder> orders = new ArrayList<>();
        PlatformOrder order = new PlatformOrder();
        order.setPlatformOrderId("OZ345678");
        order.setCustomerId("C003");
        order.setCustomerName("Demo Customer 3");
        order.setStatus("PENDING");
        order.setTotalAmount(new BigDecimal("299.99"));
        order.setPaymentStatus("PAID");
        order.setShippingStatus("PENDING_SHIPMENT");
        order.setOrderDate(LocalDateTime.now());
        orders.add(order);
        return orders;
    }

    @Override
    public List<PlatformProduct> fetchProducts() {
        try {
            List<OzonProductListResponse.Item> productListItems = fetchProductList();
            if (productListItems == null || productListItems.isEmpty()) {
                return new ArrayList<>();
            }

            List<String> productIds = productListItems.stream()
                    .map(item -> String.valueOf(item.getProduct_id()))
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .collect(Collectors.toList());

            if (productIds.isEmpty()) {
                return new ArrayList<>();
            }

            List<OzonProductInfoResponse.ProductInfo> productDetails = fetchProductDetails(productIds);

            List<PlatformProduct> platformProducts = productListItems.stream()
                    .map(this::convertToPlatformProduct)
                    .collect(Collectors.toList());

            if (productDetails != null && !productDetails.isEmpty()) {
                syncProductDetailsToDatabase(productDetails);
            }

            return platformProducts;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 获取商品列表
     */
    private List<OzonProductListResponse.Item> fetchProductList() {
        try {
            OzonProductListRequest request = new OzonProductListRequest();
            OzonProductListRequest.Filter filter = new OzonProductListRequest.Filter();
            filter.setOffer_id(Collections.emptyList());
            filter.setProduct_id(Collections.emptyList());
            filter.setVisibility("ALL");
            request.setFilter(filter);
            request.setLast_id("");
            request.setLimit(1000);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Client-Id", clientId);
            headers.set("Api-Key", apiKey);

            HttpEntity<OzonProductListRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<OzonProductListResponse> response = restTemplate.exchange(
                    PRODUCT_LIST_URL,
                    HttpMethod.POST,
                    entity,
                    OzonProductListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OzonProductListResponse.Result result = response.getBody().getResult();
                if (result != null && result.getItems() != null) {
                    return result.getItems();
                }
            }

            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 批量获取商品详情
     */
    private List<OzonProductInfoResponse.ProductInfo> fetchProductDetails(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<OzonProductInfoResponse.ProductInfo> allProductDetails = new ArrayList<>();

        // 每批最多 1000
        int batchSize = 1000;
        for (int i = 0; i < productIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, productIds.size());
            List<String> batch = productIds.subList(i, end);

            try {
                List<OzonProductInfoResponse.ProductInfo> batchDetails = fetchProductInfo(batch);
                if (batchDetails != null) {
                    allProductDetails.addAll(batchDetails);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return allProductDetails;
    }

    /**
     * 获取商品详情信息（默认 ALL，可见性可选，limit 最大 1000）
     * 可选 visibility：ALL / VISIBLE / TO_SUPPLY / IN_SALE / ARCHIVED
     */
    public List<OzonProductInfoResponse.ProductInfo> fetchProductInfo(List<String> productIds) {
        return fetchProductInfo(productIds, "ALL", 1000, null, "ASC");
    }

    /**
     * 获取商品详情信息（完整参数）
     */
    public List<OzonProductInfoResponse.ProductInfo> fetchProductInfo(
            List<String> productIds,
            String visibility,
            Integer limit,
            String lastId,
            String sortDir) {
        try {
            OzonProductInfoRequest request = new OzonProductInfoRequest();
            request.setProduct_id(productIds);
            request.setVisibility(visibility != null ? visibility : "ALL");
            int pageSize = (limit != null && limit > 0) ? Math.min(limit, 1000) : 1000;
            request.setLimit(pageSize);
            request.setLast_id(lastId != null ? lastId : "");
            request.setSort_dir(sortDir != null ? sortDir : "ASC");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Client-Id", clientId);
            headers.set("Api-Key", apiKey);

            HttpEntity<OzonProductInfoRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<OzonProductInfoResponse> response = restTemplate.exchange(
                    PRODUCT_INFO_URL,
                    HttpMethod.POST,
                    entity,
                    OzonProductInfoResponse.class
            );

            OzonProductInfoResponse responseBody = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                List<OzonProductInfoResponse.ProductInfo> result = responseBody.getResult();
                if (result != null) {
                    return result;
                }
            }

            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 同步商品详情到数据库
     */
    private void syncProductDetailsToDatabase(List<OzonProductInfoResponse.ProductInfo> productDetails) {
        if (ozonProductService == null || productDetails == null || productDetails.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (OzonProductInfoResponse.ProductInfo productInfo : productDetails) {
            try {
                OzonProduct existingItem = ozonProductService.getById(productInfo.getId());

                OzonProduct productItem = convertToProductItem(productInfo);

                if (existingItem != null) {
                    productItem.setId(existingItem.getId());
                    productItem.setCreatedAt(existingItem.getCreatedAt());
                    productItem.setUpdatedAt(now);
                    ozonProductService.update(productItem);
                } else {
                    productItem.setCreatedAt(now);
                    productItem.setUpdatedAt(now);
                    ozonProductService.save(productItem);
                }

                syncProductImages(productInfo);
                syncProductStatus(productInfo, now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将 Ozon 商品数据转换为 PlatformProduct
     */
    private PlatformProduct convertToPlatformProduct(OzonProductListResponse.Item item) {
        PlatformProduct product = new PlatformProduct();
        product.setProductId(String.valueOf(item.getProduct_id()));
        product.setSku(item.getOffer_id());
        product.setName(item.getOffer_id() != null ? "商品-" + item.getOffer_id() : "未知商品");
        product.setPrice(BigDecimal.ZERO);
        product.setStock(calculateStock(item.getQuants()));
        return product;
    }

    /**
     * 计算库存总数
     */
    private Integer calculateStock(List<OzonProductListResponse.Quant> quants) {
        if (quants == null || quants.isEmpty()) {
            return 0;
        }
        return quants.stream()
                .mapToInt(quant -> quant.getQuant_size() != null ? quant.getQuant_size() : 0)
                .sum();
    }

    /**
     * 将商品详情转为实体
     */
    private OzonProduct convertToProductItem(OzonProductInfoResponse.ProductInfo productInfo) {
        OzonProduct productItem = new OzonProduct();

        productItem.setId(productInfo.getId());
        productItem.setOfferId(productInfo.getOffer_id());
        productItem.setName(productInfo.getName());

        if (productInfo.getVolume_weight() != null) {
            productItem.setVolumeWeight(BigDecimal.valueOf(productInfo.getVolume_weight()));
        } else {
            productItem.setVolumeWeight(BigDecimal.ZERO);
        }

        productItem.setSku(productInfo.getSku());

        if (productInfo.getPrice() != null && !productInfo.getPrice().trim().isEmpty()) {
            try {
                productItem.setPrice(new BigDecimal(productInfo.getPrice().trim()));
            } catch (NumberFormatException e) {
                productItem.setPrice(BigDecimal.ZERO);
            }
        } else {
            productItem.setPrice(BigDecimal.ZERO);
        }

        if (productInfo.getOld_price() != null && !productInfo.getOld_price().trim().isEmpty()) {
            try {
                productItem.setOldPrice(new BigDecimal(productInfo.getOld_price().trim()));
            } catch (NumberFormatException e) {
                productItem.setOldPrice(null);
            }
        } else {
            productItem.setOldPrice(null);
        }

        productItem.setMinPrice(productInfo.getMin_price());
        productItem.setVat(productInfo.getVat());
        productItem.setCurrencyCode(productInfo.getCurrency_code());

        if (productInfo.getDescription_category_id() != null) {
            productItem.setDescriptionCategoryId(productInfo.getDescription_category_id().intValue());
        }

        if (productInfo.getType_id() != null) {
            productItem.setTypeId(productInfo.getType_id().intValue());
        }

        productItem.setIsArchived(productInfo.getIs_archived() != null ? productInfo.getIs_archived() : false);
        productItem.setIsAutoarchived(productInfo.getIs_autoarchived() != null ? productInfo.getIs_autoarchived() : false);
        productItem.setIsDiscounted(productInfo.getIs_discounted() != null ? productInfo.getIs_discounted() : false);
        productItem.setIsKgt(productInfo.getIs_kgt() != null ? productInfo.getIs_kgt() : false);
        productItem.setIsPrepaymentAllowed(productInfo.getIs_prepayment_allowed() != null ? productInfo.getIs_prepayment_allowed() : true);
        productItem.setIsSuper(productInfo.getIs_super() != null ? productInfo.getIs_super() : false);
        productItem.setDiscountedFboStocks(productInfo.getDiscounted_fbo_stocks() != null ? productInfo.getDiscounted_fbo_stocks() : 0);

        if (productInfo.getCreated_at() != null && !productInfo.getCreated_at().trim().isEmpty()) {
            try {
                productItem.setCreatedAt(LocalDateTime.parse(productInfo.getCreated_at().trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            } catch (Exception e) {
                productItem.setCreatedAt(null);
            }
        }

        if (productInfo.getUpdated_at() != null && !productInfo.getUpdated_at().trim().isEmpty()) {
            try {
                productItem.setUpdatedAt(LocalDateTime.parse(productInfo.getUpdated_at().trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            } catch (Exception e) {
                productItem.setUpdatedAt(null);
            }
        }

        productItem.setSyncTime(LocalDateTime.now());
        return productItem;
    }

    /**
     * 同步商品图片
     */
    private void syncProductImages(OzonProductInfoResponse.ProductInfo productInfo) {
        if (ozonProductImageService == null || productInfo == null || productInfo.getId() == null) {
            return;
        }
        Long productId = productInfo.getId();
        List<String> images = productInfo.getImages();
        List<String> colorImages = productInfo.getColor_image();

        List<com.example.shopeeerp.pojo.OzonProductImage> imageEntities = new ArrayList<>();
        int order = 0;

        if (images != null) {
            for (String url : images) {
                if (url != null && !url.trim().isEmpty()) {
                    com.example.shopeeerp.pojo.OzonProductImage entity = new com.example.shopeeerp.pojo.OzonProductImage();
                    entity.setProductId(productId);
                    entity.setImageUrl(url);
                    entity.setSortOrder(order++);
                    entity.setIsPrimary(true);
                    entity.setCreatedAt(new Date());
                    imageEntities.add(entity);
                }
            }
        }

        order = appendImages(imageEntities, images, productId, order, false);
        appendImages(imageEntities, colorImages, productId, order, false);

        try {
            ozonProductImageService.removeByProductId(productId);
            if (!imageEntities.isEmpty()) {
                ozonProductImageService.saveBatch(imageEntities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int appendImages(List<com.example.shopeeerp.pojo.OzonProductImage> target,
                             List<String> urls,
                             Long productId,
                             int startOrder,
                             boolean isPrimary) {
        if (urls == null) {
            return startOrder;
        }
        int order = startOrder;
        for (String url : urls) {
            if (url != null && !url.trim().isEmpty()) {
                com.example.shopeeerp.pojo.OzonProductImage entity = new com.example.shopeeerp.pojo.OzonProductImage();
                entity.setProductId(productId);
                entity.setImageUrl(url);
                entity.setSortOrder(order++);
                entity.setIsPrimary(isPrimary);
                entity.setCreatedAt(new Date());
                target.add(entity);
            }
        }
        return order;
    }

    /**
     * 同步商品状态
     */
    private void syncProductStatus(OzonProductInfoResponse.ProductInfo productInfo, LocalDateTime now) {
        if (ozonProductStatusService == null || productInfo == null || productInfo.getId() == null) {
            return;
        }
        OzonProductInfoResponse.Statuses statuses = productInfo.getStatuses();
        if (statuses == null) {
            return;
        }
        OzonProductStatus entity = new OzonProductStatus();
        entity.setProductId(productInfo.getId());
        entity.setIsCreated(statuses.getIs_created());
        entity.setModerateStatus(statuses.getModerate_status());
        entity.setStatus(statuses.getStatus());
        entity.setStatusDescription(statuses.getStatus_description());
        entity.setStatusFailed(statuses.getStatus_failed());
        entity.setStatusName(statuses.getStatus_name());
        entity.setStatusTooltip(statuses.getStatus_tooltip());
        entity.setValidationStatus(statuses.getValidation_status());
        entity.setStatusUpdatedAt(parseDate(statuses.getStatus_updated_at()));
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());

        try {
            OzonProductStatus existing = ozonProductStatusService.getByProductId(productInfo.getId());
            if (existing != null) {
                entity.setId(existing.getId());
                entity.setCreatedAt(existing.getCreatedAt());
                ozonProductStatusService.update(entity);
            } else {
                ozonProductStatusService.save(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date parseDate(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            LocalDateTime time = LocalDateTime.parse(value.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return Date.from(time.atZone(java.time.ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PlatformCost fetchCost(String productId) {
        PlatformCost cost = new PlatformCost();
        cost.setProductId(productId);
        cost.setCostPrice(new BigDecimal("150.00"));
        cost.setShippingCost(new BigDecimal("20.00"));
        cost.setPlatformFee(new BigDecimal("10.00"));
        cost.setTotalCost(new BigDecimal("180.00"));
        return cost;
    }
}
