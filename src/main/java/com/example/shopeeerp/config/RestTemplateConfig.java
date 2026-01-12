package com.example.shopeeerp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Central RestTemplate configuration with sane defaults for connect/read timeouts.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${ozon.http.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${ozon.http.read-timeout-ms:20000}")
    private int readTimeoutMs;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
