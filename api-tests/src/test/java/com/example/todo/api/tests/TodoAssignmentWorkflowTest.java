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
import java.util.List;

@Epic("Todo Management")
@Feature("Todo Assignment")
public class TodoAssignmentWorkflowTest extends BaseTest {

    private UserClient userClient;
    private AuthClient authClient;
    private TodoClient todoClient;
    private AssignmentClient assignmentClient;
    private NotificationClient notificationClient;

    @BeforeClass
    public void setup() {
        userClient = new UserClient();
        authClient = new AuthClient();
        todoClient = new TodoClient();
        assignmentClient = new AssignmentClient();
        notificationClient = new NotificationClient();
    }

    @Test(description = "Full Todo Assignment Happy Path Workflow")
    @Story("As a user, I want to assign a todo and get a response")
    @Step("Happy Path Workflow")
    public void fullTodoAssignmentHappyPath() {
        // Step 1: Register User A and User B
        UserDto userA = registerNewUser();
        UserDto userB = registerNewUser();

        // Step 2: User A logs in
        String tokenA = authClient.getAuthToken(userA.getUsername(), userA.getPassword());
        Assert.assertNotNull(tokenA, "Token for User A should not be null");

        // Step 3: User A creates a new "Todo"
        String description = "Task for " + userB.getUsername() + " " + System.currentTimeMillis();
        TodoDto todo = TodoDto.builder()
                .description(description)
                .targetDate(LocalDate.now().plusDays(7))
                .done(false)
                .build();
        todoClient.createTodo(todo, tokenA);
        
        // Fetch todo list to get ID
        Response listResponseA = todoClient.getTodos(tokenA);
        Integer todoId = TestUtils.extractTodoIdFromHtml(listResponseA.getBody().asString(), description);
        Assert.assertNotNull(todoId, "Todo ID should be found");

        // Step 4: User A assigns the "Todo" to User B
        assignmentClient.assignTodo(todoId, userB.getUsername(), tokenA)
                .then().statusCode(200);

        // Step 5: User B logs in
        String tokenB = authClient.getAuthToken(userB.getUsername(), userB.getPassword());
        Assert.assertNotNull(tokenB, "Token for User B should not be null");

        // Step 6: User B fetches their notifications
        List<NotificationDTO> notificationsB = notificationClient.getUnreadNotifications(tokenB)
                .jsonPath().getList(".", NotificationDTO.class);
        
        boolean hasAssignmentNotification = notificationsB.stream()
                .anyMatch(n -> n.getMessage().contains("has assigned you a new todo"));
        Assert.assertTrue(hasAssignmentNotification, "User B should receive assignment notification");

        // Step 7: User B accepts the assignment
        Response listResponseB = todoClient.getTodos(tokenB);
        Long assignmentId = TestUtils.extractAssignmentIdFromHtml(listResponseB.getBody().asString(), description);
        Assert.assertNotNull(assignmentId, "Assignment ID should be found for User B");
        
        RespondAssignmentRequestDto respondRequest = RespondAssignmentRequestDto.builder()
                .action("accept")
                .tentativeCompletionDate(LocalDate.now().plusDays(5))
                .build();
        assignmentClient.respondToAssignment(assignmentId, respondRequest, tokenB)
                .then().statusCode(200);

        // Step 8: User A logs in (already have tokenA)
        // Step 9: User A fetches their notifications
        List<NotificationDTO> notificationsA = notificationClient.getUnreadNotifications(tokenA)
                .jsonPath().getList(".", NotificationDTO.class);
        
        boolean hasAcceptedNotification = notificationsA.stream()
                .anyMatch(n -> n.getMessage().contains("accepted your assignment"));
        Assert.assertTrue(hasAcceptedNotification, "User A should receive acceptance notification");
    }

    @Test(description = "Todo Assignment Decline Path Workflow")
    @Story("As a user, I want to decline an assignment")
    public void todoAssignmentDeclinePath() {
        UserDto userA = registerNewUser();
        UserDto userB = registerNewUser();
        
        String tokenA = authClient.getAuthToken(userA.getUsername(), userA.getPassword());
        String description = "Task to decline " + System.currentTimeMillis();
        TodoDto todo = TodoDto.builder().description(description).targetDate(LocalDate.now().plusDays(7)).done(false).build();
        todoClient.createTodo(todo, tokenA);
        
        Integer todoId = TestUtils.extractTodoIdFromHtml(todoClient.getTodos(tokenA).getBody().asString(), description);
        Assert.assertNotNull(todoId, "Todo ID should be found for decline test");
        
        assignmentClient.assignTodo(todoId, userB.getUsername(), tokenA);
        
        String tokenB = authClient.getAuthToken(userB.getUsername(), userB.getPassword());
        
        // Get Assignment ID
        Long assignmentId = TestUtils.extractAssignmentIdFromHtml(todoClient.getTodos(tokenB).getBody().asString(), description);
        Assert.assertNotNull(assignmentId, "Assignment ID should be found for User B in decline test");
        
        // Decline
        RespondAssignmentRequestDto respondRequest = RespondAssignmentRequestDto.builder()
                .action("decline")
                .declineReason("I am too busy")
                .build();
        assignmentClient.respondToAssignment(assignmentId, respondRequest, tokenB)
                .then().statusCode(200);
        
        // Check User A notification
        List<NotificationDTO> notificationsA = notificationClient.getUnreadNotifications(tokenA)
                .jsonPath().getList(".", NotificationDTO.class);
        
        boolean hasDeclinedNotification = notificationsA.stream()
                .anyMatch(n -> n.getMessage().contains("declined your assignment"));
        Assert.assertTrue(hasDeclinedNotification, "User A should receive declined notification");
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