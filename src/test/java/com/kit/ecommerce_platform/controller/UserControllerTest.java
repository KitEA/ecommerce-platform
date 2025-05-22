package com.kit.ecommerce_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kit.ecommerce_platform.config.ControllerTestConfig;
import com.kit.ecommerce_platform.config.NoSecurityConfig;
import com.kit.ecommerce_platform.config.UserTestConfig;
import com.kit.ecommerce_platform.dto.AuthRequest;
import com.kit.ecommerce_platform.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({ControllerTestConfig.class, UserTestConfig.class, NoSecurityConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenSignup_shouldReturnSuccessMessage() throws Exception {
        // given
        AuthRequest request = new AuthRequest("newuser", "password", "test@gmail.com");

        doNothing().when(userService).registerUser(any());

        // when/then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void whenLogin_shouldReturnToken() throws Exception {
        // given
        AuthRequest request = new AuthRequest("john", "secret", "test@gmail.com");
        String mockToken = "jwt-mock-token";

        when(userService.login(any())).thenReturn(mockToken);

        // when/then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockToken));
    }
}
