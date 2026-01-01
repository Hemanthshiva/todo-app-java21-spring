package com.learn.spring.todoapp.integration;

import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Create a test user for authentication tests
        User user = new User("securityuser", passwordEncoder.encode("password"), "security@example.com");
        userRepository.save(user);
        authorityRepository.addAuthority("securityuser", "ROLE_USER");
    }

    @Test
    void loginWithValidCredentials_ShouldAuthenticateUser() throws Exception {
        mockMvc.perform(formLogin().user("securityuser").password("password"))
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));
    }

    @Test
    void loginWithInvalidCredentials_ShouldFailAuthentication() throws Exception {
        mockMvc.perform(formLogin().user("securityuser").password("wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void accessProtectedResourceWithoutAuthentication_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "securityuser")
    void accessProtectedResourceWithAuthentication_ShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-testid=\"todo-table\"")));
    }

    @Test
    void registerNewUser_ShouldCreateUserAndRedirectToWelcome() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "newuser")
                .param("password", "password")
                .param("confirmPassword", "password")
                .param("email", "new@example.com")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void accessAdminResource_ShouldBeAllowed() throws Exception {
        // This test assumes there's an admin-only endpoint
        // If there isn't one, you can skip this test or modify it
        // For demonstration purposes. We'll use a non-existent endpoint
        mockMvc.perform(get("/admin"))
                .andExpect(status().isNotFound()); // 404 because the endpoint doesn't exist
    }

    @Test
    @WithMockUser(username = "user")
    void accessAdminResourceAsUser_ShouldBeForbidden() throws Exception {
        // This test assumes there's an admin-only endpoint
        // If there isn't one, you can skip this test or modify it
        // For demonstration purposes; we'll use a non-existent endpoint
        mockMvc.perform(get("/admin"))
                .andExpect(status().isNotFound()); // 404 because the endpoint doesn't exist
    }

    @Test
    void logoutUser_ShouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}
