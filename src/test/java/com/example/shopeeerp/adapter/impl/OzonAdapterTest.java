package com.example.shopeeerp.adapter.impl;

import com.example.shopeeerp.adapter.dto.ozon.OzonCashflowRequest;
import com.example.shopeeerp.adapter.dto.ozon.OzonCashflowResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OzonAdapterTest {

    private RestTemplate restTemplate;
    private OzonAdapter adapter;

    @BeforeEach
    void setUp() {
        restTemplate = org.mockito.Mockito.mock(RestTemplate.class);
        adapter = new OzonAdapter(restTemplate, null, null, null);
        ReflectionTestUtils.setField(adapter, "cashflowMaxRetries", 0);
        ReflectionTestUtils.setField(adapter, "cashflowRetryBackoffMs", 0L);
        ReflectionTestUtils.setField(adapter, "clientId", "client");
        ReflectionTestUtils.setField(adapter, "apiKey", "key");
    }

    @Test
    void fetchCashflowsShouldPaginateAndReturnAllPages() {
        OzonCashflowResponse first = new OzonCashflowResponse();
        first.setPageCount(2);
        first.setResult(new OzonCashflowResponse.Result());

        OzonCashflowResponse second = new OzonCashflowResponse();
        second.setPageCount(2);
        second.setResult(new OzonCashflowResponse.Result());

        when(restTemplate.exchange(
                eq("https://api-seller.ozon.ru/v1/finance/cash-flow-statement/list"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(OzonCashflowResponse.class)
        )).thenReturn(ResponseEntity.ok(first), ResponseEntity.ok(second));

        List<OzonCashflowResponse> responses = adapter.fetchCashflows(
                "2025-09-01T00:00:00Z",
                "2025-09-30T00:00:00Z",
                true,
                50
        );

        assertThat(responses).hasSize(2);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(2)).exchange(
                eq("https://api-seller.ozon.ru/v1/finance/cash-flow-statement/list"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(OzonCashflowResponse.class)
        );

        OzonCashflowRequest requestPage1 = (OzonCashflowRequest) captor.getAllValues().get(0).getBody();
        OzonCashflowRequest requestPage2 = (OzonCashflowRequest) captor.getAllValues().get(1).getBody();

        assertThat(requestPage1.getPage()).isEqualTo(1);
        assertThat(requestPage2.getPage()).isEqualTo(2);
        assertThat(requestPage1.getWithDetails()).isTrue();
        assertThat(requestPage1.getDate().getFrom()).isEqualTo("2025-09-01T00:00:00Z");
        assertThat(requestPage1.getDate().getTo()).isEqualTo("2025-09-30T00:00:00Z");
    }
}
