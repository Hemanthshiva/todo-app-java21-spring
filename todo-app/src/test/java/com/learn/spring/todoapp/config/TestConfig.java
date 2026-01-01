package com.learn.spring.todoapp.config;

import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import com.learn.spring.todoapp.service.UserInitializer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")  // Create tables first
                .addScript("classpath:data.sql")    // Then insert data
                .build();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Provide a mock AuthorityRepository for tests that use @DataJpaTest
     * This is not used by AuthorityRepositoryTest, which uses the real implementation
     */
    @Bean
    @Primary
    public AuthorityRepository authorityRepository(JdbcTemplate jdbcTemplate) {
        // For AuthorityRepositoryTest, we need a real implementation
        if (isAuthorityRepositoryTest()) {
            return new AuthorityRepository(jdbcTemplate);
        }
        // For other tests, use a mock
        return Mockito.mock(AuthorityRepository.class);
    }

    private boolean isAuthorityRepositoryTest() {
        // Check if the current test class is AuthorityRepositoryTest
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().contains("AuthorityRepositoryTest")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provide a mock PasswordEncoder for tests that use @DataJpaTest
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }

    /**
     * Create a no-op UserInitializer for tests to prevent database initialization
     */
    @Bean
    @Primary
    public UserInitializer userInitializer(UserRepository userRepository,
                                           AuthorityRepository authorityRepository,
                                           PasswordEncoder passwordEncoder) {
        return new UserInitializer(userRepository, authorityRepository, passwordEncoder) {
            @Override
            public void init() {
                // Do nothing in tests
                System.out.println("Test UserInitializer: Skipping database initialization");
            }
        };
    }
}
