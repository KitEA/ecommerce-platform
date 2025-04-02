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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
        // given
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        CartItem cartItem = cartService.addProductToCart(user.getId(), product.getId(), 1);

        // then
        assertThat(cartItem).isNotNull();
        assertThat(1).isEqualTo(cartItem.getQuantity());
        assertThat(product).isEqualTo(cartItem.getProduct());
        assertThat(cart).isEqualTo(cartItem.getCart());

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void whenAddProductWithZeroQuantity_shouldThrowException() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> cartService.addProductToCart(user.getId(), product.getId(), 0))
                .withMessageContaining("Quantity must be positive");
    }

    @Test
    void whenAddExistingProductToCart_shouldUpdateQuantity() {
        // given
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
        CartItem updatedItem = cartService.addProductToCart(user.getId(), product.getId(), 2);

        // then
        assertThat(3).isEqualTo(updatedItem.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }
}