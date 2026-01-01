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

@Epic("Notifications")
@Feature("Notification Management")
public class NotificationLifecycleTest extends BaseTest {

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

    @Test(description = "Verify Notification Lifecycle (Receive -> Read)")
    @Story("As a user, I want to manage my notifications")
    public void verifyNotificationLifecycle() {
        // 1. Setup Users
        UserDto userA = registerNewUser();
        UserDto userB = registerNewUser();
        String tokenA = authClient.getAuthToken(userA.getUsername(), userA.getPassword());
        
        // 2. User A creates Todo and Assigns to B
        String description = "Notification Task " + System.currentTimeMillis();
        TodoDto todo = TodoDto.builder()
                .description(description)
                .targetDate(LocalDate.now().plusDays(7))
                .done(false)
                .build();
        todoClient.createTodo(todo, tokenA);
        
        Response listResponseA = todoClient.getTodos(tokenA);
        Integer todoId = TestUtils.extractTodoIdFromHtml(listResponseA.getBody().asString(), description);
        
        assignmentClient.assignTodo(todoId, userB.getUsername(), tokenA);

        // 3. User B checks notifications
        String tokenB = authClient.getAuthToken(userB.getUsername(), userB.getPassword());
        List<NotificationDTO> notifications = notificationClient.getUnreadNotifications(tokenB)
                .jsonPath().getList(".", NotificationDTO.class);
        
        NotificationDTO targetNotification = notifications.stream()
                .filter(n -> n.getMessage().contains("has assigned you a new todo"))
                .findFirst()
                .orElse(null);
        
        Assert.assertNotNull(targetNotification, "Should receive assignment notification");

        // 4. Mark as Read
        notificationClient.markAsRead(targetNotification.getId(), tokenB)
                .then().statusCode(200);

        // 5. Verify it's gone from unread list
        List<NotificationDTO> notificationsAfter = notificationClient.getUnreadNotifications(tokenB)
                .jsonPath().getList(".", NotificationDTO.class);
        
        boolean stillExists = notificationsAfter.stream()
                .anyMatch(n -> n.getId().equals(targetNotification.getId()));
        Assert.assertFalse(stillExists, "Notification should be removed from unread list");
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
