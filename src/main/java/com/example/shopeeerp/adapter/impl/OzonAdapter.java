package com.example.shopeeerp.adapter.impl;

import com.example.shopeeerp.adapter.PlatformAdapter;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductListRequest;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductListResponse;
import com.example.shopeeerp.adapter.model.PlatformCost;
import com.example.shopeeerp.adapter.model.PlatformOrder;
import com.example.shopeeerp.adapter.model.PlatformProduct;
import com.example.shopeeerp.pojo.Product;
import com.example.shopeeerp.service.ProductService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ozon平台适配器实现
 */
@Component
public class OzonAdapter implements PlatformAdapter {
    
    private static final String OZON_API_BASE_URL = "https://api-seller.ozon.ru";
    private static final String PRODUCT_LIST_URL = OZON_API_BASE_URL + "/v3/product/list";
    
    private final RestTemplate restTemplate;
    
    @Autowired(required = false)
    private ProductService productService;
    
    @Value("${ozon.api.client-id:}")
    private String clientId;
    
    @Value("${ozon.api.api-key:}")
    private String apiKey;
    
    public OzonAdapter() {
        this.restTemplate = new RestTemplate();
    }
    
    // 用于测试的构造函数
    public OzonAdapter(RestTemplate restTemplate, String clientId, String apiKey) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.apiKey = apiKey;
    }
    
    // 用于测试的构造函数（包含ProductService）
    public OzonAdapter(RestTemplate restTemplate, String clientId, String apiKey, ProductService productService) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.productService = productService;
    }
    
    @Override
    public String getPlatformName() {
        return "Ozon";
    }
    
    @Override
    public List<PlatformOrder> fetchOrders(String startDate, String endDate) {
        // TODO: 实现Ozon API调用
        // 这里应该调用Ozon的API来获取订单数据
        // 参考Ozon API文档：https://docs.ozon.ru/api/seller/
        List<PlatformOrder> orders = new ArrayList<>();
        
        // 示例订单
        PlatformOrder order = new PlatformOrder();
        order.setPlatformOrderId("OZ345678");
        order.setCustomerId("C003");
        order.setCustomerName("示例客户3");
        order.setStatus("待处理");
        order.setTotalAmount(new BigDecimal("299.99"));
        order.setPaymentStatus("已支付");
        order.setShippingStatus("待发货");
        order.setOrderDate(LocalDateTime.now());
        
        orders.add(order);
        
        return orders;
    }
    
    @Override
    public List<PlatformProduct> fetchProducts() {
        try {
            // 构建请求对象
            OzonProductListRequest request = new OzonProductListRequest();
            OzonProductListRequest.Filter filter = new OzonProductListRequest.Filter();
            filter.setOffer_id(Collections.emptyList());
            filter.setProduct_id(Collections.emptyList());
            filter.setVisibility("ALL");
            request.setFilter(filter);
            request.setLast_id("");
            request.setLimit(100);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Client-Id", clientId);
            headers.set("Api-Key", apiKey);
            
            HttpEntity<OzonProductListRequest> entity = new HttpEntity<>(request, headers);
            
            // 发送请求
            ResponseEntity<OzonProductListResponse> response = restTemplate.exchange(
                    PRODUCT_LIST_URL,
                    HttpMethod.POST,
                    entity,
                    OzonProductListResponse.class
            );
            
            // 转换响应数据
            OzonProductListResponse responseBody = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                OzonProductListResponse.Result result = responseBody.getResult();
                if (result != null && result.getItems() != null) {
                    List<PlatformProduct> platformProducts = result.getItems().stream()
                            .map(this::convertToPlatformProduct)
                            .collect(Collectors.toList());
                    
                    // 同步商品数据到数据库
                    syncProductsToDatabase(platformProducts);
                    
                    return platformProducts;
                }
            }
            
            return new ArrayList<>();
        } catch (Exception e) {
            // 如果API调用失败，返回空列表或记录日志
            // 在实际生产环境中，应该记录详细的错误日志
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 将Ozon商品数据转换为PlatformProduct
     */
    private PlatformProduct convertToPlatformProduct(OzonProductListResponse.Item item) {
        PlatformProduct product = new PlatformProduct();
        product.setProductId(String.valueOf(item.getProduct_id()));
        product.setSku(item.getOffer_id());
        // 注意：Ozon API的product/list接口返回的数据中没有name、price等信息
        // 这些信息需要通过其他API获取（如/product/info）
        product.setName(item.getOffer_id() != null ? "商品-" + item.getOffer_id() : "未知商品");
        product.setPrice(BigDecimal.ZERO); // 需要通过其他API获取价格
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
     * 将平台商品同步到数据库
     */
    private void syncProductsToDatabase(List<PlatformProduct> platformProducts) {
        if (productService == null || platformProducts == null || platformProducts.isEmpty()) {
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        for (PlatformProduct platformProduct : platformProducts) {
            try {
                // 跳过SKU为空的商品
                if (platformProduct.getSku() == null || platformProduct.getSku().trim().isEmpty()) {
                    continue;
                }
                
                // 根据SKU查询是否已存在
                Product existingProduct = productService.selectBySku(platformProduct.getSku());
                
                Product product = convertToProduct(platformProduct);
                
                if (existingProduct != null) {
                    // 更新已存在的商品
                    product.setProductId(existingProduct.getProductId());
                    product.setCreatedAt(existingProduct.getCreatedAt()); // 保留原始创建时间
                    product.setUpdatedAt(now);
                    productService.update(product);
                } else {
                    // 插入新商品
                    product.setCreatedAt(now);
                    product.setUpdatedAt(now);
                    productService.insert(product);
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他商品
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 将PlatformProduct转换为Product
     */
    private Product convertToProduct(PlatformProduct platformProduct) {
        Product product = new Product();
        product.setSku(platformProduct.getSku());
        // 确保name不为空，如果为空则使用SKU作为名称
        String name = platformProduct.getName();
        if (name == null || name.trim().isEmpty()) {
            name = platformProduct.getSku() != null ? "商品-" + platformProduct.getSku() : "未知商品";
        }
        product.setName(name);
        product.setDescription(platformProduct.getDescription());
        product.setPrice(platformProduct.getPrice() != null ? platformProduct.getPrice() : BigDecimal.ZERO);
        if(Strings.isNotEmpty(platformProduct.getProductId())){
            product.setProductId(Long.valueOf(platformProduct.getProductId()));
        }
        // categoryId 暂时设为null，后续可以根据需要设置
        product.setCategoryId(null);
        return product;
    }
    
    @Override
    public PlatformCost fetchCost(String productId) {
        // TODO: 实现Ozon API调用
        // 这里应该调用Ozon的API来获取成本数据
        // 参考Ozon API文档：https://docs.ozon.ru/api/seller/
        PlatformCost cost = new PlatformCost();
        cost.setProductId(productId);
        cost.setCostPrice(new BigDecimal("150.00"));
        cost.setShippingCost(new BigDecimal("20.00"));
        cost.setPlatformFee(new BigDecimal("10.00"));
        cost.setTotalCost(new BigDecimal("180.00"));
        
        return cost;
    }
}
