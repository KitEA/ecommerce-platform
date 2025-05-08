package com.kit.ecommerce_platform.dto;

import lombok.*;

@Value
@Builder
public class ProductSearchCriteria {
    String name;
    Double minPrice;
    Double maxPrice;
    String category;
    Boolean inStock;
}
