package com.kit.ecommerce_platform.config;

import com.kit.ecommerce_platform.service.CartService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class CartTestConfig {

    @Bean
    public CartService cartService() {
        return mock(CartService.class);
    }
}
