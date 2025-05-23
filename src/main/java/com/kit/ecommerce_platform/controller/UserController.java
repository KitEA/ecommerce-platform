package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.dto.LoginRequest;
import com.kit.ecommerce_platform.dto.SignUpRequest;
import com.kit.ecommerce_platform.dto.LoginResponse;
import com.kit.ecommerce_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpRequest request) {
        userService.registerUser(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return new LoginResponse(token);
    }
}
