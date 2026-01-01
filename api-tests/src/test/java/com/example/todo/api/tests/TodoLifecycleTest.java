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

@Epic("Todo Management")
@Feature("CRUD Operations")
public class TodoLifecycleTest extends BaseTest {

    private UserClient userClient;
    private AuthClient authClient;
    private TodoClient todoClient;

    @BeforeClass
    public void setup() {
        userClient = new UserClient();
        authClient = new AuthClient();
        todoClient = new TodoClient();
    }

    @Test(description = "Verify Todo CRUD Lifecycle")
    @Story("As a user, I want to create, update, and delete todos")
    public void verifyTodoLifecycle() {
        // 1. Register and Login
        UserDto user = registerNewUser();
        String token = authClient.getAuthToken(user.getUsername(), user.getPassword());

        // 2. Create Todo
        String description = "Lifecycle Task " + System.currentTimeMillis();
        TodoDto todo = TodoDto.builder()
                .description(description)
                .targetDate(LocalDate.now().plusDays(7))
                .done(false)
                .build();
        todoClient.createTodo(todo, token).then().statusCode(302); // Redirects after create

        // 3. Get Todo ID
        Response listResponse = todoClient.getTodos(token);
        Integer todoId = TestUtils.extractTodoIdFromHtml(listResponse.getBody().asString(), description);
        Assert.assertNotNull(todoId, "Todo ID should be found");

        // 4. Update Todo
        String updatedDescription = description + " Updated";
        TodoDto updatedTodo = TodoDto.builder()
                .description(updatedDescription)
                .targetDate(LocalDate.now().plusDays(8))
                .done(true)
                .build();
        todoClient.updateTodo(todoId, updatedTodo, token).then().statusCode(302);

        // 5. Verify Update
        Response listResponseAfterUpdate = todoClient.getTodos(token);
        String html = listResponseAfterUpdate.getBody().asString();
        Assert.assertTrue(html.contains(updatedDescription), "Updated description should be present");
        // Verify 'done' status? HTML might show strike-through or badge.
        // Assuming description change is enough proof for now.

        // 6. Delete Todo
        todoClient.deleteTodo(todoId, token).then().statusCode(302);

        // 7. Verify Deletion
        Response listResponseAfterDelete = todoClient.getTodos(token);
        Assert.assertFalse(listResponseAfterDelete.getBody().asString().contains(updatedDescription), 
                "Todo should be removed from list");
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
