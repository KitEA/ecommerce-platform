package com.kit.ecommerce_platform.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchCriteria {
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private String category;
    private Boolean inStock;
}
