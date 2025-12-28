package com.learn.spring.todoapp.repository;

import com.learn.spring.todoapp.config.AuthorityRepositoryTestConfig;
import com.learn.spring.todoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(AuthorityRepositoryTestConfig.class)
@ActiveProfiles("test")
@Transactional
public class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    void addAuthority_ShouldAddAuthorityForUser() {
        // When
        authorityRepository.addAuthority("testuser", "ROLE_TEST");

        // Then
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("testuser");
        assertEquals(1, authorities.size());
        assertEquals("ROLE_TEST", authorities.getFirst());
    }

    @Test
    void removeAuthority_ShouldRemoveSpecificAuthorityForUser() {
        // Given
        authorityRepository.addAuthority("testuser", "ROLE_TEST1");
        authorityRepository.addAuthority("testuser", "ROLE_TEST2");

        // When
        authorityRepository.removeAuthority("testuser", "ROLE_TEST1");

        // Then
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("testuser");
        assertEquals(1, authorities.size());
        assertEquals("ROLE_TEST2", authorities.getFirst());
    }

    @Test
    void removeAllAuthorities_ShouldRemoveAllAuthoritiesForUser() {
        // Given
        authorityRepository.addAuthority("testuser", "ROLE_TEST1");
        authorityRepository.addAuthority("testuser", "ROLE_TEST2");

        // When
        authorityRepository.removeAllAuthorities("testuser");

        // Then
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("testuser");
        assertTrue(authorities.isEmpty());
    }

    @Test
    void findAuthoritiesByUsername_ShouldReturnAllAuthoritiesForUser() {
        // Given
        authorityRepository.addAuthority("testuser", "ROLE_TEST1");
        authorityRepository.addAuthority("testuser", "ROLE_TEST2");

        // When
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("testuser");

        // Then
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_TEST1"));
        assertTrue(authorities.contains("ROLE_TEST2"));
    }

    @Test
    void findAuthoritiesByUsername_ShouldReturnEmptyListForUserWithNoAuthorities() {
        // When
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("testuser");

        // Then
        assertTrue(authorities.isEmpty());
    }

    @Test
    void findAuthoritiesByUsername_ShouldReturnEmptyListForNonExistentUser() {
        // When
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("nonexistent");

        // Then
        assertTrue(authorities.isEmpty());
    }
}
