package com.kit.ecommerce_platform.service;

import com.kit.ecommerce_platform.dto.AuthRequest;
import com.kit.ecommerce_platform.model.User;
import com.kit.ecommerce_platform.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterNewUserSuccessfully() {
        // given
        AuthRequest request = new AuthRequest("newuser", "password");

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
        AuthRequest request = new AuthRequest("existing", "pass");

        when(userRepository.findByUsername("existing"))
                .thenReturn(Optional.of(new User()));

        // when/then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> userService.registerUser(request))
                .withMessageContaining("Username already taken");
    }

    @Test
    void shouldAuthenticateWithCorrectCredentials() {
        AuthRequest request = new AuthRequest("user", "secret");

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
        AuthRequest request = new AuthRequest("user", "wrong");

        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        boolean result = userService.authenticateUser(request);
        assertThat(result).isFalse();
    }
}
