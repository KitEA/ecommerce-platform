package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.ProductSearchCriteria;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.model.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;

    public List<Product> searchProducts(ProductSearchCriteria criteria) {
        if (criteria.name() != null) {
            return productRepository.findByNameContainingIgnoreCase(criteria.name());
        }

        if (criteria.minPrice() != null || criteria.maxPrice() != null) {
            BigDecimal min = Optional.ofNullable(criteria.minPrice())
                    .map(BigDecimal::valueOf)
                    .orElse(BigDecimal.ZERO);

            BigDecimal max = Optional.ofNullable(criteria.maxPrice())
                    .map(BigDecimal::valueOf)
                    .orElse(new BigDecimal("1000000"));

            return productRepository.findByPriceBetween(min, max);
        }

        return productRepository.findAll();
    }
}
