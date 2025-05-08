package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.CartRequest;
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
    public CartItem addProductToCart(CartRequest cartRequest) {
        int quantity = cartRequest.getQuantity();
        validateQuantity(quantity);

        Cart cart = getCartByUserId(cartRequest.getUserId());
        Product product = getProductById(cartRequest.getProductId());

        return cartItemRepository.findByCartIdAndProductId(cart.getId(), cartRequest.getProductId())
                .map(item -> updateQuantity(item, quantity))
                .orElseGet(() -> createNewCartItem(cart, product, quantity));
    }

    @Transactional
    public void removeProductFromCart(CartRequest cartRequest) {
        Cart cart = cartRepository.findByUserId(cartRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        cartItemRepository.findByCartIdAndProductId(cart.getId(), cartRequest.getProductId())
                .ifPresent(item -> cart.getItems().remove(item));
    }

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Transactional
    public void checkout(Long cartId) {
        Cart cart = getCart(cartId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout empty cart.");
        }

        // integrate with payment processor here.

        cart.getItems().clear();
        cartRepository.save(cart);
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
