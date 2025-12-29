package com.learn.spring.todoapp.service;

import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class UserInitializer {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, 
                          AuthorityRepository authorityRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // Ensure default users exist on startup
        createUserIfNotExists("admin", "admin123", "admin@example.com", true);
        createUserIfNotExists("user1", "user123", "user1@example.com", false);
        createUserIfNotExists("user2", "user123", "user2@example.com", false);
    }

    private void createUserIfNotExists(String username, String rawPassword, String email, boolean isAdmin) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, passwordEncoder.encode(rawPassword), email);
            userRepository.save(user);

            // Assign authorities
            authorityRepository.addAuthority(username, "ROLE_USER");
            if (isAdmin) {
                authorityRepository.addAuthority(username, "ROLE_ADMIN");
            }

            System.out.println("Default user created: " + user.getUsername());
        }
    }
}
