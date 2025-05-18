package com.kit.ecommerce_platform.config;

import com.kit.ecommerce_platform.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class UserTestConfig {

    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }
}
