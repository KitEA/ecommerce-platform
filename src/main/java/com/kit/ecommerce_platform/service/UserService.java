package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.AuthRequest;
import com.kit.ecommerce_platform.model.User;
import com.kit.ecommerce_platform.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(AuthRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())).build();

        userRepository.save(user);
    }

    public boolean authenticateUser(AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .map(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .orElse(false);
    }
}
