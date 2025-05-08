package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.config.MockServiceConfig;
import com.kit.ecommerce_platform.config.NoSecurityConfig;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({MockServiceConfig.class, NoSecurityConfig.class})
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
}