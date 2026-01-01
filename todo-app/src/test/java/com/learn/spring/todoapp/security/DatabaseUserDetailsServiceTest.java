package com.learn.spring.todoapp.security;

import com.learn.spring.todoapp.config.DatabaseUserDetailsServiceTestConfig;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DatabaseUserDetailsServiceTestConfig.class)
@ActiveProfiles("test")
@Transactional
public class DatabaseUserDetailsServiceTest {

    @Autowired
    private DatabaseUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User("testuser", "password", "test@example.com");
        userRepository.save(testUser);

        // Clean up any existing authorities for this user
        authorityRepository.removeAllAuthorities("testuser");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsForExistingUser() {
        // Given
        authorityRepository.addAuthority("testuser", "ROLE_TEST");

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_TEST")));
    }

    @Test
    void loadUserByUsername_ShouldAddDefaultRoleWhenNoAuthoritiesExist() {
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowExceptionForNonExistentUser() {
        // When/Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsWithMultipleAuthorities() {
        // Given
        authorityRepository.addAuthority("testuser", "ROLE_USER");
        authorityRepository.addAuthority("testuser", "ROLE_ADMIN");

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ShouldRespectEnabledFlag() {
        // Given
        testUser.setEnabled(false);
        userRepository.save(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertFalse(userDetails.isEnabled());
    }
}
