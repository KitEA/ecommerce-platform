package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.CartRequest;
import com.kit.ecommerce_platform.model.Cart;
import com.kit.ecommerce_platform.model.CartItem;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.model.User;
import com.kit.ecommerce_platform.model.repository.CartItemRepository;
import com.kit.ecommerce_platform.model.repository.CartRepository;
import com.kit.ecommerce_platform.model.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Product product2;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .email("test@example.com").build();
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-end laptop")
                .price(BigDecimal.valueOf(999.99))
                .sku("LP-001")
                .isActive(true).build();
        product2 = Product.builder()
                .id(1L)
                .name("PC")
                .description("High-end PC")
                .price(BigDecimal.valueOf(1099.99))
                .sku("PP-002")
                .isActive(true).build();
        cart = Cart.builder().user(user).build();
    }

    @Test
    void whenAddNewProductToCart_shouldCreateCartItem() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(user.getId())
                .productId(product.getId())
                .quantity(1).build();
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        CartItem cartItem = cartService.addProductToCart(cartRequest);

        // then
        assertThat(cartItem).isNotNull();
        assertThat(1).isEqualTo(cartItem.getQuantity());
        assertThat(product).isEqualTo(cartItem.getProduct());
        assertThat(cart).isEqualTo(cartItem.getCart());

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void whenAddProductWithZeroQuantity_shouldThrowException() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(user.getId())
                .productId(product.getId())
                .quantity(0).build();

        // when/then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> cartService.addProductToCart(cartRequest))
                .withMessageContaining("Quantity must be positive");
    }

    @Test
    void whenAddExistingProductToCart_shouldUpdateQuantity() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(user.getId())
                .productId(product.getId())
                .quantity(2).build();
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(1).build();
        cart.getItems().add(existingItem);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        CartItem updatedItem = cartService.addProductToCart(cartRequest);

        // then
        assertThat(3).isEqualTo(updatedItem.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void whenRemoveExistingProductFromCart_shouldDeleteCartItem() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(user.getId())
                .productId(product.getId()).build();
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();
        cart.getItems().add(existingItem);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(existingItem));

        // when
        cartService.removeProductFromCart(cartRequest);

        // then
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void whenRemovedNonExistentProduct_shouldDoNothing() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(user.getId())
                .productId(999L).build();
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(any(), any()))
                .thenReturn(Optional.empty());

        // when/then
        assertThatNoException().isThrownBy(() -> cartService.removeProductFromCart(cartRequest));
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void whenRemoveFromNonExistentCart_shouldThrowException() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(999L)
                .productId(product.getId()).build();
        when(cartRepository.findByUserId(any())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> cartService.removeProductFromCart(cartRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cart not found");
    }

    @Test
    void whenRemoveItemFromTheCart_shouldRemoveSpecifiedItemFromTheCart() {
        // given
        CartRequest cartRequest = CartRequest.builder()
                .userId(user.getId())
                .productId(product.getId()).build();
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(1).build();
        CartItem cartItem2 = CartItem.builder()
                .cart(cart)
                .product(product2)
                .quantity(2).build();

        cart.getItems().addAll(List.of(cartItem, cartItem2));

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(cartItem));

        // when
        cartService.removeProductFromCart(cartRequest);

        // then
        assertThat(cart.getItems())
                .hasSize(1)
                .containsExactly(cartItem2);
    }

    @Test
    void whenRemoveItem_shouldAutoDeleteViaOrphanRemoval() {
        CartItem item = CartItem.builder().cart(cart).product(product).build();
        cart.getItems().add(item);
        cartRepository.save(cart); // Persist with item

        cart.getItems().remove(item); // Should trigger deletion
        cartRepository.flush(); // Force DB sync

        assertThat(cartItemRepository.findById(item.getId())).isEmpty(); // Verify deleted
    }

    @Test
    void whenGetCart_shouldReturnCart_whenCartExists() {
        // given
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));

        // when
        Cart result = cartService.getCart(cart.getId());

        // then
        assertThat(result).isEqualTo(cart);
    }

    @Test
    void whenGetCart_shouldThrowException_whenCartNotFound() {
        // given
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> cartService.getCart(cart.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cart not found");
    }

    @Test
    void whenCheckout_shouldClearCart_whenCartHasItems() {
        // given
        CartItem item = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(1)
                .build();

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cart.getId())).thenReturn(List.of(item));

        // when
        cartService.checkout(cart.getId());

        // then
        verify(cartItemRepository).findByCartId(cart.getId());
        verify(cartItemRepository).deleteByCartId(cart.getId());
    }

    @Test
    void whenCheckout_shouldThrowException_whenCartIsEmpty() {
        // given
        Cart emptyCart = Cart.builder().id(1L).items(new ArrayList<>()).build();

        when(cartRepository.findById(emptyCart.getId())).thenReturn(Optional.of(emptyCart));

        assertThatThrownBy(() -> cartService.checkout(emptyCart.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot checkout empty cart.");
    }
}