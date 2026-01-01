package com.example.todo.api.tests;

import com.example.todo.api.clients.AuthClient;
import com.example.todo.api.clients.UserClient;
import com.example.todo.api.models.UserDto;
import com.example.todo.api.utils.TestUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("User Management")
@Feature("Registration and Authentication")
public class UserManagementTest extends BaseTest {

    private UserClient userClient;
    private AuthClient authClient;

    @BeforeClass
    public void setup() {
        userClient = new UserClient();
        authClient = new AuthClient();
    }

    @Test(description = "Should register a new user successfully")
    @Story("User Registration")
    public void shouldRegisterNewUserSuccessfully() {
        UserDto user = createRandomUser();

        Response response = userClient.registerUser(user);
        Assert.assertEquals(response.getStatusCode(), 302);
        Assert.assertTrue(response.getHeader("Location").contains("/welcome"));
    }
    
    @Test(description = "Should fail to register with existing username")
    public void shouldFailToRegisterWithExistingUsername() {
        UserDto user = createRandomUser();
        userClient.registerUser(user); // First registration
        
        Response response = userClient.registerUser(user); // Second registration
        Assert.assertEquals(response.getStatusCode(), 200); // Returns to form
        // In a real HTML test we would check for error message, but checking it didn't redirect is good for now
        // Or check body for "Username already exists"
        Assert.assertTrue(response.getBody().asString().contains("Username already exists"));
    }
    
    @Test(description = "Should login successfully and receive token")
    public void shouldLoginSuccessfullyAndReceiveToken() {
        UserDto user = createRandomUser();
        userClient.registerUser(user);
        
        String token = authClient.getAuthToken(user.getUsername(), user.getPassword());
        Assert.assertNotNull(token, "Auth token should not be null");
    }
    
    @Test(description = "Should fail login with invalid credentials")
    public void shouldFailLoginWithInvalidCredentials() {
        String token = authClient.getAuthToken("nonexistent", "wrongpass");
        Assert.assertNull(token, "Auth token should be null for invalid credentials");
    }
    
    private UserDto createRandomUser() {
        return UserDto.builder()
                .username(TestUtils.generateRandomUsername())
                .password("password")
                .confirmPassword("password")
                .email(TestUtils.generateRandomEmail())
                .build();
    }
}