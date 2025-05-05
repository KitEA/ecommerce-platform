package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.ProductSearchCriteria;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.model.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductSearchServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    @Test
    void whenSearchByName_shouldReturnMatchingProducts() {
        // given
        Product laptop = Product.builder()
                .name("MacBook Pro")
                .price(BigDecimal.valueOf(1999)).build();
        when(productRepository.findByNameContainingIgnoreCase("mac"))
                .thenReturn(List.of(laptop));
        ProductSearchCriteria nameCriteria = ProductSearchCriteria.builder()
                .name("mac").build();

        // when
        List<Product> results = productSearchService.searchProducts(nameCriteria);

        // then
        assertThat(results).hasSize(1).containsExactly(laptop);
    }

    @Test
    void whenSearchByPriceRange_shouldFilterProducts() {
        //given
        Product cheapLaptop = Product.builder()
                .name("Budget Laptop")
                .price(BigDecimal.valueOf(499)).build();
        when(productRepository.findByPriceBetween(BigDecimal.valueOf(400.0), BigDecimal.valueOf(600.0)))
                .thenReturn(List.of(cheapLaptop));
        ProductSearchCriteria priceRangeCriteria = ProductSearchCriteria.builder()
                .minPrice(400.0)
                .maxPrice(600.0).build();

        // when
        List<Product> results = productSearchService.searchProducts(priceRangeCriteria);

        // then
        assertThat(results).hasSize(1).extracting(Product::getName).contains("Budget Laptop");
    }
}
