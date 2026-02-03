package com.example.shopeeerp.config;

import com.example.shopeeerp.monitor.SlowQueryInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {

    @Autowired
    private SlowQueryInterceptor slowQueryInterceptor;

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> configuration.addInterceptor(slowQueryInterceptor);
    }
}
