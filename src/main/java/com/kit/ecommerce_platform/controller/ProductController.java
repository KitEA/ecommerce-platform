package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.dto.ProductSearchCriteria;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSearchService searchService;

    @GetMapping("/search")
    public List<Product> searchProducts(@ModelAttribute ProductSearchCriteria criteria) {
        return searchService.searchProducts(criteria);
    }
}