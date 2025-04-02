package com.kit.ecommerce_platform.model.repository;

import com.kit.ecommerce_platform.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
