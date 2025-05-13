package com.kit.ecommerce_platform.config;

import com.kit.ecommerce_platform.service.ProductSearchService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ProductTestConfig {

    @Bean
    public ProductSearchService productSearchService() {
        return mock(ProductSearchService.class);
    }
}
