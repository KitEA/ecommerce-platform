package com.kit.ecommerce_platform.dto;

import lombok.*;

@Builder
public record ProductSearchCriteria (String name, Double minPrice, Double maxPrice, String category, Boolean inStock) {}
