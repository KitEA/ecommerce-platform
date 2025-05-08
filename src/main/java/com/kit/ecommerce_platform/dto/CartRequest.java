package com.kit.ecommerce_platform.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CartRequest {
    Long userId;
    Long productId;
    int quantity;
}
