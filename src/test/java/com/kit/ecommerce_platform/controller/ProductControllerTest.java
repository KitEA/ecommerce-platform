package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductSearchService searchService;

    @Test
    void whenSearchEndpointCalled_shouldReturn200() throws Exception {
        // given
        when(searchService.searchProducts(any()))
                .thenReturn(List.of(
                        Product.builder()
                                .name("MacBook Pro").build()
                ));

        // when/then
        mockMvc.perform(get("/api/products/search")
                        .param("name", "mac"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("MacBook Pro"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        ProductSearchService productSearchService() {
            return mock(ProductSearchService.class);
        }
    }
}