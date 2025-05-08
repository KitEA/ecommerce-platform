package com.kit.ecommerce_platform.dto;

import lombok.Builder;

@Builder
public record CartRequest(Long userId, Long productId, int quantity) {
}
