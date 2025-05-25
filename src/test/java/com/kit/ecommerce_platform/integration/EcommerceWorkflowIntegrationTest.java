package com.kit.ecommerce_platform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.ecommerce_platform.dto.*;
import com.kit.ecommerce_platform.model.*;
import com.kit.ecommerce_platform.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EcommerceWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private String jwtToken;
    private Long userId;
    private Long macbookId;
    private Long iphoneId;

    @BeforeEach
    void setUp() {
        // Clean up all data before each test
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        // Create test products once per test
        createTestProducts();
    }

    @Test
    void shouldCompleteFullEcommerceWorkflow() throws Exception {
        // Integration test focusing on component interactions and data flow

        // 1. User Registration -> Should create User + manually create Cart
        registerUserAndCreateCart();

        // 2. Authentication -> Should generate valid JWT
        authenticateAndGetToken();

        // 3. Product Search + Cart Operations -> Should persist across requests
        performShoppingWorkflow();

        // 4. Verify final state across all components
        verifyWorkflowIntegrity();
    }

    private void registerUserAndCreateCart() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest(
                "integrationuser",
                "password123",
                "integration@test.com"
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());

        // Get the created user
        User user = userRepository.findByUsername("integrationuser").orElseThrow();
        userId = user.getId();

        // Manually create cart since it's not automatically created in your service
        Cart cart = Cart.builder()
                .user(user)
                .build();
        cartRepository.save(cart);

        // Verify cart was created
        Cart userCart = cartRepository.findByUserId(userId).orElseThrow();
        assertThat(userCart.getUser().getId()).isEqualTo(userId);
        assertThat(userCart.getItems()).isEmpty();
    }

    private void authenticateAndGetToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("integrationuser", "password123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LoginResponse.class
        );
        jwtToken = response.token();
        assertThat(jwtToken).isNotNull();
    }

    private void performShoppingWorkflow() throws Exception {
        // Add products to cart using authenticated requests
        addProductToCart(macbookId, 1);
        addProductToCart(iphoneId, 2);

        // Verify cart state persists between requests using direct repository query
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        var cartItems = cartItemRepository.findByCartId(cart.getId());
        assertThat(cartItems).hasSize(2);

        // Remove one product and verify persistence
        removeProductFromCart(iphoneId);

        // Verify removal - this should work after fixing the service
        cartItems = cartItemRepository.findByCartId(cart.getId());
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.get(0).getProduct().getId()).isEqualTo(macbookId);
    }

    private void verifyWorkflowIntegrity() throws Exception {
        // Verify the entire workflow maintained data integrity
        Cart finalCart = cartRepository.findByUserId(userId).orElseThrow();

        // Test cart retrieval endpoint with final state
        mockMvc.perform(get("/api/cart/" + finalCart.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(finalCart.getId()));

        // Verify using direct repository query
        var finalItems = cartItemRepository.findByCartId(finalCart.getId());
        assertThat(finalItems).hasSize(1);
        assertThat(finalItems.get(0).getProduct().getName()).isEqualTo("MacBook Pro");
        assertThat(finalItems.get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void shouldHandleCheckoutWorkflow() throws Exception {
        // Test the complete checkout process
        registerUserAndCreateCart();
        authenticateAndGetToken();
        addProductToCart(macbookId, 1);

        Cart cart = cartRepository.findByUserId(userId).orElseThrow();

        // Verify item was added using direct repository query
        var cartItems = cartItemRepository.findByCartId(cart.getId());
        assertThat(cartItems).hasSize(1);

        // Perform checkout
        mockMvc.perform(post("/api/cart/" + cart.getId() + "/checkout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        // Verify cart is cleared after checkout
        var clearedItems = cartItemRepository.findByCartId(cart.getId());
        assertThat(clearedItems).isEmpty();
    }

    @Test
    void shouldHandleSecurityWorkflow() throws Exception {
        // Test that security works end-to-end

        // Unauthenticated requests should fail (403 is also acceptable for Spring Security)
        mockMvc.perform(get("/api/products/search"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // After authentication, requests should work
        registerUserAndCreateCart();
        authenticateAndGetToken();

        mockMvc.perform(get("/api/products/search")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    // Helper methods
    private void addProductToCart(Long productId, int quantity) throws Exception {
        CartRequest request = CartRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build();

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private void removeProductFromCart(Long productId) throws Exception {
        CartRequest request = CartRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(1)
                .build();

        mockMvc.perform(delete("/api/cart/remove")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private void createTestProducts() {
        Product macbook = productRepository.save(Product.builder()
                .name("MacBook Pro")
                .description("Apple laptop with M1 chip")
                .price(new BigDecimal("1999.99"))
                .sku("MBP-13-M1-" + System.currentTimeMillis())
                .isActive(true)
                .build());

        Product iphone = productRepository.save(Product.builder()
                .name("iPhone 15")
                .description("Latest iPhone model")
                .price(new BigDecimal("999.99"))
                .sku("IPH-15-" + System.currentTimeMillis())
                .isActive(true)
                .build());

        macbookId = macbook.getId();
        iphoneId = iphone.getId();
    }
}