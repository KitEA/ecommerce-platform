package com.kit.ecommerce_platform.controller;

import com.kit.ecommerce_platform.dto.AuthRequest;
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
    public String signup(@RequestBody AuthRequest request) {
        userService.registerUser(request);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        boolean success = userService.authenticateUser(request);
        return success ? "Login successful" : "Invalid credentials";
    }
}
