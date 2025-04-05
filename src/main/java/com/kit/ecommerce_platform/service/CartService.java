package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.model.Cart;
import com.kit.ecommerce_platform.model.CartItem;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.model.repository.CartItemRepository;
import com.kit.ecommerce_platform.model.repository.CartRepository;
import com.kit.ecommerce_platform.model.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItem addProductToCart(Long userId, Long productId, int quantity) {
        validateQuantity(quantity);

        Cart cart = getCartByUserId(userId);
        Product product = getProductById(productId);

        return cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .map(item -> updateQuantity(item, quantity))
                .orElseGet(() -> createNewCartItem(cart, product, quantity));
    }

    @Transactional
    public void removeProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresent(item -> {
                    cart.getItems().remove(item);
                });
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private CartItem updateQuantity(CartItem item, int quantity) {
        item.setQuantity(item.getQuantity() + quantity);
        return cartItemRepository.save(item);
    }

    private CartItem createNewCartItem(Cart cart, Product product, int quantity) {
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity).build();
        return cartItemRepository.save(cartItem);
    }
}
