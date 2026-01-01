package com.learn.spring.todoapp.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class AuthorityRepository {

    private static final Logger logger = Logger.getLogger(AuthorityRepository.class.getName());
    private final JdbcTemplate jdbcTemplate;

    public AuthorityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        // Ensure the authorities table exists
        try {
            createAuthoritiesTableIfNotExists();
        } catch (Exception e) {
            logger.warning("Failed to create authorities table: " + e.getMessage());
        }
    }

    private void createAuthoritiesTableIfNotExists() {
        try {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS authorities (" +
                "username VARCHAR(255) NOT NULL, " +
                "authority VARCHAR(255) NOT NULL, " +
                "CONSTRAINT pk_authorities PRIMARY KEY (username, authority))");
        } catch (DataAccessException e) {
            logger.warning("Could not create authorities table: " + e.getMessage());
        }
    }

    public void addAuthority(String username, String authority) {
        try {
            String sql = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
            int rowsAffected = jdbcTemplate.update(sql, username, authority);
            System.out.println("[DEBUG_LOG] Added authority: " + authority + " for user: " + username + ", rows affected: " + rowsAffected);
        } catch (DataAccessException e) {
            System.out.println("[DEBUG_LOG] Failed to add authority: " + e.getMessage());
            logger.warning("Failed to add authority: " + e.getMessage());
        }
    }

    public void removeAuthority(String username, String authority) {
        try {
            String sql = "DELETE FROM authorities WHERE username = ? AND authority = ?";
            jdbcTemplate.update(sql, username, authority);
        } catch (DataAccessException e) {
            logger.warning("Failed to remove authority: " + e.getMessage());
        }
    }

    public void removeAllAuthorities(String username) {
        try {
            String sql = "DELETE FROM authorities WHERE username = ?";
            jdbcTemplate.update(sql, username);
        } catch (DataAccessException e) {
            logger.warning("Failed to remove all authorities: " + e.getMessage());
        }
    }

    public List<String> findAuthoritiesByUsername(String username) {
        try {
            String sql = "SELECT authority FROM authorities WHERE username = ?";
            List<String> authorities = jdbcTemplate.queryForList(sql, String.class, username);
            System.out.println("[DEBUG_LOG] Found authorities for user: " + username + ", count: " + authorities.size() + ", authorities: " + authorities);
            return authorities;
        } catch (DataAccessException e) {
            System.out.println("[DEBUG_LOG] Failed to find authorities: " + e.getMessage());
            logger.warning("Failed to find authorities: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
