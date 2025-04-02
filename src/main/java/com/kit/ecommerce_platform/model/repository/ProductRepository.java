package com.kit.ecommerce_platform.model.repository;

import com.kit.ecommerce_platform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
