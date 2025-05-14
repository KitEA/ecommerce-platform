package com.kit.ecommerce_platform;

import com.kit.ecommerce_platform.config.ControllerTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Import(ControllerTestConfig.class)
class EcommercePlatformApplicationTests {

    @Test
    void contextLoads() {
    }

}
