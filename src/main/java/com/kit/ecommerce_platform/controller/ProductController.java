package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.dto.ProductSearchCriteria;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(ProductSearchCriteria criteria) {
        return ResponseEntity.ok(
                searchService.searchProducts(criteria)
        );
    }
}