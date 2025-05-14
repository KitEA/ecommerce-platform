package com.kit.ecommerce_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.ecommerce_platform.config.CartTestConfig;
import com.kit.ecommerce_platform.config.ControllerTestConfig;
import com.kit.ecommerce_platform.config.NoSecurityConfig;
import com.kit.ecommerce_platform.dto.CartRequest;
import com.kit.ecommerce_platform.model.Cart;
import com.kit.ecommerce_platform.model.CartItem;
import com.kit.ecommerce_platform.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import({ControllerTestConfig.class, CartTestConfig.class, NoSecurityConfig.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenAddToCart_shouldAddProductToTheCart() throws Exception {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(2).build();
        CartItem cartItem = CartItem.builder()
                .id(100L)
                .quantity(2).build();

        when(cartService.addProductToCart(cartRequest)).thenReturn(cartItem);

        // when/then
        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void whenRemoveFromCart_shouldRemoveProductFromTheCart() throws Exception {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(0).build();

        doNothing().when(cartService).removeProductFromCart(cartRequest);

        // when/then
        mockMvc.perform(delete("/api/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void whenViewCart_shouldReturnCart() throws Exception {
        // given
        Cart cart = Cart.builder().id(1L).items(List.of()).build();

        when(cartService.getCart(cart.getId())).thenReturn(cart);

        // when/then
        mockMvc.perform(get("/api/cart/{cartId}", cart.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cart.getId()));
    }

    @Test
    void whenCheckout_shouldReturnSuccessMessage() throws Exception {
        // given
        Long cartId = 1L;

        doNothing().when(cartService).checkout(cartId);

        // when/then
        mockMvc.perform(post("/api/cart/{cartId}/checkout", cartId))
                .andExpect(status().isOk())
                .andExpect(content().string("Checkout complete. Payment processed."));
    }
}