package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.AuthRequest;
import com.kit.ecommerce_platform.model.User;
import com.kit.ecommerce_platform.model.repository.UserRepository;
import com.kit.ecommerce_platform.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterNewUserSuccessfully() {
        // given
        AuthRequest request = new AuthRequest("newuser", "password", "test@gmail.com");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");

        // when
        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        // then
        assertThat("newuser").isEqualTo(saved.getUsername());
        assertThat("encoded").isEqualTo(saved.getPassword());
    }

    @Test
    void shouldThrowExceptionIfUsernameAlreadyExists() {
        // given
        AuthRequest request = new AuthRequest("existing", "pass", "test@gmail.com");

        when(userRepository.findByUsername("existing"))
                .thenReturn(Optional.of(new User()));

        // when/then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> userService.registerUser(request))
                .withMessageContaining("Username already taken");
    }

    @Test
    void shouldAuthenticateWithCorrectCredentials() {
        AuthRequest request = new AuthRequest("user", "secret", "test@gmail.com");

        User user = User.builder()
                .username("user")
                .password("encoded").build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);

        boolean result = userService.authenticateUser(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldRejectInvalidLogin() {
        AuthRequest request = new AuthRequest("user", "wrong", "test@gmail.com");

        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        boolean result = userService.authenticateUser(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnToken_whenCredentialsAreValid() {
        // given
        String username = "john";
        String rawPassword = "secret";
        String randomEmail = "test@gmail.com";
        String hashedPassword = "hashedSecret";
        String token = "mock-token";

        AuthRequest request = new AuthRequest(username, rawPassword, randomEmail);
        User user = User.builder()
                .username(username)
                .password(hashedPassword)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(username)).thenReturn(token);

        // when
        String result = userService.login(request);

        // then
        assertThat(result).isEqualTo(token);
    }

    @Test
    void shouldThrowBadCredentialsException_whenCredentialsAreInvalid() {
        // given
        AuthRequest request = new AuthRequest("john", "wrong-password", "test@gmail.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
    }
}
