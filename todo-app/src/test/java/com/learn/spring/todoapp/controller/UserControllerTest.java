package com.learn.spring.todoapp.controller;

import com.learn.spring.todoapp.config.UserControllerTestConfig;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTestConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthorityRepository authorityRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void showRegistrationForm_ShouldDisplayRegistrationPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void showLoginForm_ShouldDisplayLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerUserAccount_ShouldCreateUserAndRedirect() throws Exception {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User("testuser", "encodedPassword", "test@example.com"));
        doNothing().when(authorityRepository).addAuthority(anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
        verify(authorityRepository, times(1)).addAuthority("testuser", "ROLE_USER");
    }

    @Test
    void registerUserAccount_ShouldFailWhenUsernameExists() throws Exception {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "username"));

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerUserAccount_ShouldFailWhenPasswordsDontMatch() throws Exception {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        // When/Then
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("confirmPassword", "different")
                        .param("email", "test@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "confirmPassword"));

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerUserAccount_ShouldFailWhenEmailIsInvalid() throws Exception {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        // When/Then
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("email", "invalid-email")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));

        verify(userRepository, times(0)).save(any(User.class));
    }
}
