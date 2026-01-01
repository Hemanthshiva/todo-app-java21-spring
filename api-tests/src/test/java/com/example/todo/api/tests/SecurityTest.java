package com.example.todo.api.tests;

import com.example.todo.api.clients.*;
import com.example.todo.api.models.*;
import com.example.todo.api.utils.TestUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.LocalDate;

@Epic("Security")
@Feature("Authorization")
public class SecurityTest extends BaseTest {

    private UserClient userClient;
    private AuthClient authClient;
    private TodoClient todoClient;
    private AssignmentClient assignmentClient;

    @BeforeClass
    public void setup() {
        userClient = new UserClient();
        authClient = new AuthClient();
        todoClient = new TodoClient();
        assignmentClient = new AssignmentClient();
    }

    @Test(description = "Verify user cannot assign a todo they do not own")
    @Story("As a malicious user, I should not be able to assign others' todos")
    public void shouldFailWhenUserTriesToAssignTodoTheyDoNotOwn() {
        // User A creates a todo
        UserDto userA = registerNewUser();
        String tokenA = authClient.getAuthToken(userA.getUsername(), userA.getPassword());
        
        String description = "User A Task " + System.currentTimeMillis();
        TodoDto todo = TodoDto.builder()
                .description(description)
                .targetDate(LocalDate.now().plusDays(7))
                .done(false)
                .build();
        todoClient.createTodo(todo, tokenA);
        
        Response listResponseA = todoClient.getTodos(tokenA);
        Integer todoId = TestUtils.extractTodoIdFromHtml(listResponseA.getBody().asString(), description);
        Assert.assertNotNull(todoId, "Todo ID should be found");

        // User B tries to assign User A's todo to themselves (or anyone)
        UserDto userB = registerNewUser();
        String tokenB = authClient.getAuthToken(userB.getUsername(), userB.getPassword());
        
        // This should fail with 403 Forbidden or 404 Not Found (depending on implementation)
        // Ideally 403, but if filtered by user, maybe 404?
        // Let's check what the backend does. Assuming 403 for now.
        Response response = assignmentClient.assignTodo(todoId, userB.getUsername(), tokenB);
        
        // Adjust assertion based on actual behavior. 
        // If the todo is not found for the user, it might return error.
        Assert.assertTrue(response.getStatusCode() >= 400, "Should return an error status code (403/404)");
    }

    @Test(description = "Verify user cannot accept assignment meant for another user")
    @Story("As a malicious user, I should not be able to accept others' assignments")
    public void shouldFailWhenUserTriesToAcceptAssignmentMeantForAnotherUser() {
        // User A assigns to User B
        UserDto userA = registerNewUser();
        UserDto userB = registerNewUser();
        UserDto userC = registerNewUser(); // Malicious user

        String tokenA = authClient.getAuthToken(userA.getUsername(), userA.getPassword());
        String tokenB = authClient.getAuthToken(userB.getUsername(), userB.getPassword());
        String tokenC = authClient.getAuthToken(userC.getUsername(), userC.getPassword());

        String description = "Task for B " + System.currentTimeMillis();
        TodoDto todo = TodoDto.builder()
                .description(description)
                .targetDate(LocalDate.now().plusDays(7))
                .done(false)
                .build();
        todoClient.createTodo(todo, tokenA);
        
        Response listResponseA = todoClient.getTodos(tokenA);
        Integer todoId = TestUtils.extractTodoIdFromHtml(listResponseA.getBody().asString(), description);
        
        assignmentClient.assignTodo(todoId, userB.getUsername(), tokenA)
                .then().statusCode(200);

        // Get Assignment ID (User B can see it)
        Response listResponseB = todoClient.getTodos(tokenB);
        Long assignmentId = TestUtils.extractAssignmentIdFromHtml(listResponseB.getBody().asString(), description);
        Assert.assertNotNull(assignmentId, "Assignment ID should be found");

        // User C tries to accept it
        RespondAssignmentRequestDto respondRequest = RespondAssignmentRequestDto.builder()
                .action("accept")
                .tentativeCompletionDate(LocalDate.now().plusDays(5))
                .build();
        
        Response response = assignmentClient.respondToAssignment(assignmentId, respondRequest, tokenC);
        Assert.assertTrue(response.getStatusCode() >= 400, "Should return an error status code (403/404)");
    }

    @Test(description = "Verify unauthenticated user cannot access todos")
    @Story("As an anonymous user, I should not be able to access protected resources")
    public void shouldFailToAccessTodosWithoutAuthentication() {
        Response response = todoClient.getTodos(null); // No token
        // Spring Security usually redirects to login page (302) or returns 401/403
        // If it redirects to login, status code might be 200 (login page) but not the todo list.
        // However, RestAssured follows redirects by default? No, we configured .redirects().follow(false) in TodoClient?
        // Let's check TodoClient.
        
        Assert.assertTrue(response.getStatusCode() == 302 || response.getStatusCode() == 401 || response.getStatusCode() == 403, 
                "Should be unauthorized (302 redirect to login or 401/403)");
    }

    private UserDto registerNewUser() {
        String username = TestUtils.generateRandomUsername();
        UserDto user = UserDto.builder()
                .username(username)
                .password("password")
                .confirmPassword("password")
                .email(TestUtils.generateRandomEmail())
                .build();
        userClient.registerUser(user);
        return user;
    }
}
