package com.example.shopeeerp.adapter.impl;

import com.example.shopeeerp.adapter.dto.ozon.OzonProductInfoResponse;
import com.example.shopeeerp.adapter.dto.ozon.OzonProductListResponse;
import com.example.shopeeerp.adapter.model.PlatformProduct;
import com.example.shopeeerp.service.OzonProductImageService;
import com.example.shopeeerp.service.OzonProductService;
import com.example.shopeeerp.service.OzonProductStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OzonAdapter单元测试
 */
@ExtendWith(MockitoExtension.class)
class OzonAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OzonProductService ozonProductService;
    @Autowired(required = false)
    OzonProductImageService ozonProductImageService;
    @Autowired(required = false)
    OzonProductStatusService ozonProductStatusService;
    private OzonAdapter ozonAdapter;

    private static final String CLIENT_ID = "3207535";
    private static final String API_KEY = "f81516e3-7ab9-46d6-aaf7-6ceb404880b1";

    @BeforeEach
    void setUp() {
        ozonAdapter = new OzonAdapter(restTemplate, ozonProductService,ozonProductImageService,ozonProductStatusService);
        // 设置私有字段的值（通过反射或直接设置）
        try {
            java.lang.reflect.Field clientIdField = OzonAdapter.class.getDeclaredField("clientId");
            clientIdField.setAccessible(true);
            clientIdField.set(ozonAdapter, CLIENT_ID);

            java.lang.reflect.Field apiKeyField = OzonAdapter.class.getDeclaredField("apiKey");
            apiKeyField.setAccessible(true);
            apiKeyField.set(ozonAdapter, API_KEY);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set test fields", e);
        }
    }

    @Test
    void testFetchProducts() {
        // 准备商品列表API的测试数据
        OzonProductListResponse listResponse = new OzonProductListResponse();
        OzonProductListResponse.Result result = new OzonProductListResponse.Result();

        // 创建第一个商品
        OzonProductListResponse.Item item1 = new OzonProductListResponse.Item();
        item1.setProduct_id(223681945L);
        item1.setOffer_id("136748");

        OzonProductListResponse.Quant quant1 = new OzonProductListResponse.Quant();
        quant1.setQuant_code("CODE1");
        quant1.setQuant_size(30);

        OzonProductListResponse.Quant quant2 = new OzonProductListResponse.Quant();
        quant2.setQuant_code("CODE2");
        quant2.setQuant_size(20);

        item1.setQuants(Arrays.asList(quant1, quant2));

        // 创建第二个商品
        OzonProductListResponse.Item item2 = new OzonProductListResponse.Item();
        item2.setProduct_id(223681946L);
        item2.setOffer_id("136749");

        OzonProductListResponse.Quant quant3 = new OzonProductListResponse.Quant();
        quant3.setQuant_code("CODE3");
        quant3.setQuant_size(100);

        item2.setQuants(Arrays.asList(quant3));

        result.setItems(Arrays.asList(item1, item2));
        listResponse.setResult(result);

        ResponseEntity<OzonProductListResponse> listResponseEntity =
            new ResponseEntity<>(listResponse, HttpStatus.OK);

        // 准备商品详情API的测试数据
        OzonProductInfoResponse infoResponse = new OzonProductInfoResponse();
        List<OzonProductInfoResponse.ProductInfo> productInfos = new ArrayList<>();

        // 创建第一个商品的详细信息
        OzonProductInfoResponse.ProductInfo info1 = new OzonProductInfoResponse.ProductInfo();
        info1.setId(223681945L);
        info1.setOffer_id("136748");
        info1.setName("Test Product 1");
        info1.setPrice("100.50");
        info1.setCurrency_code("RUB");
        info1.setVat("20");
        info1.setVolume_weight(2);
        info1.setIs_archived(false);
        info1.setIs_discounted(true);
        info1.setDiscounted_fbo_stocks(10);

        // 创建第二个商品的详细信息
        OzonProductInfoResponse.ProductInfo info2 = new OzonProductInfoResponse.ProductInfo();
        info2.setId(223681946L);
        info2.setOffer_id("136749");
        info2.setName("Test Product 2");
        info2.setPrice("200.75");
        info2.setCurrency_code("RUB");
        info2.setVat("20");
        info2.setVolume_weight(3);
        info2.setIs_archived(false);
        info2.setIs_discounted(false);
        info2.setDiscounted_fbo_stocks(5);

        productInfos.add(info1);
        productInfos.add(info2);
        infoResponse.setItems(productInfos);

        ResponseEntity<OzonProductInfoResponse> infoResponseEntity =
            new ResponseEntity<>(infoResponse, HttpStatus.OK);

        // Mock 商品列表API调用
        when(restTemplate.exchange(
            eq("https://api-seller.ozon.ru/v3/product/list"),
            eq(HttpMethod.POST),
            any(),
            eq(OzonProductListResponse.class)
        )).thenReturn(listResponseEntity);

        // Mock 商品详情API调用
        when(restTemplate.exchange(
            eq("https://api-seller.ozon.ru/v3/product/info/list"),
            eq(HttpMethod.POST),
            any(),
            eq(OzonProductInfoResponse.class)
        )).thenReturn(infoResponseEntity);

        // 执行测试
        List<PlatformProduct> products = ozonAdapter.fetchProducts();

        // 验证结果
        assertNotNull(products);
        assertEquals(2, products.size());

        // 验证第一个商品
        PlatformProduct product1 = products.get(0);
        assertEquals("223681945", product1.getProductId());
        assertEquals("136748", product1.getSku());
        assertEquals("商品-136748", product1.getName());
        assertEquals(50, product1.getStock()); // 30 + 20

        // 验证第二个商品
        PlatformProduct product2 = products.get(1);
        assertEquals("223681946", product2.getProductId());
        assertEquals("136749", product2.getSku());
        assertEquals("商品-136749", product2.getName());
        assertEquals(100, product2.getStock());

        // 验证API调用次数
        verify(restTemplate, times(1)).exchange(
            eq("https://api-seller.ozon.ru/v3/product/list"),
            eq(HttpMethod.POST),
            any(),
            eq(OzonProductListResponse.class)
        );

        verify(restTemplate, times(1)).exchange(
            eq("https://api-seller.ozon.ru/v3/product/info/list"),
            eq(HttpMethod.POST),
            any(),
            eq(OzonProductInfoResponse.class)
        );
    }
}
