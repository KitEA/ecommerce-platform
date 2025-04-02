package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.model.Cart;
import com.kit.ecommerce_platform.model.CartItem;
import com.kit.ecommerce_platform.model.Product;
import com.kit.ecommerce_platform.model.User;
import com.kit.ecommerce_platform.model.repository.CartItemRepository;
import com.kit.ecommerce_platform.model.repository.CartRepository;
import com.kit.ecommerce_platform.model.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
        cart = Cart.builder().user(user).build();
    }

    @Test
    void whenAddNewProductToCart_shouldCreateCartItem() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartItem cartItem = cartService.addProductToCart(user.getId(), product.getId(), 1);

        assertThat(cartItem).isNotNull();
        assertThat(1).isEqualTo(cartItem.getQuantity());
        assertThat(product).isEqualTo(cartItem.getProduct());
        assertThat(cart).isEqualTo(cartItem.getCart());

        verify(cartItemRepository).save(any(CartItem.class));
    }
}