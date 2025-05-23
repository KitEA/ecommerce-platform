package com.kit.ecommerce_platform.model.repository;

import com.kit.ecommerce_platform.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);
}
