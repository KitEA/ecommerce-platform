package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.dto.CartRequest;
import com.kit.ecommerce_platform.model.Cart;
import com.kit.ecommerce_platform.model.CartItem;
import com.kit.ecommerce_platform.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public CartItem addToCart(@RequestBody CartRequest cartRequest) {
        return cartService.addProductToCart(cartRequest);
    }

    @DeleteMapping("/remove")
    public void removeFromCart(@RequestBody CartRequest cartRequest) {
        cartService.removeProductFromCart(cartRequest);
    }

    @GetMapping("/{cartId}")
    public Cart viewCart(@PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/{cartId}/checkout")
    public String checkout(@PathVariable Long cartId) {
        cartService.checkout(cartId);
        return "Checkout complete. Payment processed.";
    }
}
