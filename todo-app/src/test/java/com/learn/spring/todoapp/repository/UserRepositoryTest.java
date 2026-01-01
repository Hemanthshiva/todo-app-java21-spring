package com.learn.spring.todoapp.repository;

import com.learn.spring.todoapp.config.TestConfig;
import com.learn.spring.todoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User("testuser", "password", "test@example.com");
        userRepository.save(testUser);
    }

    @Test
    void findByUsername_ShouldReturnUserWithGivenUsername() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptionalForNonExistentUsername() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_ShouldReturnUserWithGivenEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void existsByUsername_ShouldReturnTrueForExistingUsername() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_ShouldReturnFalseForNonExistentUsername() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void existsByEmail_ShouldReturnTrueForExistingEmail() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalseForNonExistentEmail() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void save_ShouldCreateNewUser() {
        // Given
        User newUser = new User("newuser", "password", "new@example.com");

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
        
        // Verify it's in the database
        Optional<User> foundUser = userRepository.findByUsername("newuser");
        assertTrue(foundUser.isPresent());
    }

    @Test
    void save_ShouldUpdateExistingUser() {
        // Given
        testUser.setEmail("updated@example.com");

        // When
        User updatedUser = userRepository.save(testUser);

        // Then
        assertEquals("updated@example.com", updatedUser.getEmail());
        
        // Verify it's updated in the database
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("updated@example.com", foundUser.get().getEmail());
    }
}