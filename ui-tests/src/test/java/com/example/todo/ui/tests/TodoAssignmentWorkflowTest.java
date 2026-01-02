package com.example.todo.ui.tests;

import com.example.todo.ui.base.BaseTest;
import com.example.todo.ui.pages.AddTodoPage;
import com.example.todo.ui.pages.LoginPage;
import com.example.todo.ui.pages.RegisterPage;
import com.example.todo.ui.pages.TodoListPage;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Feature("Todo Assignment Workflow")
public class TodoAssignmentWorkflowTest extends BaseTest {
    private Faker faker = new Faker();

    @Test
    @Description("Full Todo Assignment UI Workflow (Happy Path)")
    public void fullTodoAssignmentWorkflow() {
        // Generate credentials
        String userA = faker.name().username();
        String userB = faker.name().username();
        String password = "Password123!";
        String emailA = faker.internet().emailAddress();
        String emailB = faker.internet().emailAddress();
        String todoDescription = "Task for " + userB;
        String futureDate = LocalDate.now().plusDays(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Register User A and B
        registerUser(userA, emailA, password);
        registerUser(userB, emailB, password);

        // Log in as User A and create Todo
        LoginPage loginPage = new LoginPage(driver);
        TodoListPage todoListPage = loginPage.loginAs(userA, password);
        AddTodoPage addTodoPage = todoListPage.clickAddTodo();
        todoListPage = addTodoPage.addNewTodo(todoDescription, LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Assign to User B
        assignTodoToUser(todoListPage, todoDescription, userB);

        // Log out
        todoListPage.logout();

        // Log in as User B and Accept
        todoListPage = loginPage.loginAs(userB, password);
        verifyNotificationAndAccept(todoListPage, todoDescription, futureDate);

        // Log out and back as User A
        todoListPage.logout();
        todoListPage = loginPage.loginAs(userA, password);

        // Verify notification for accepted assignment
        verifyAssignmentAcceptedNotification(todoListPage, userB, todoDescription);
    }

    @Test
    @Description("Todo Assignment Decline Workflow")
    public void todoAssignmentDeclineWorkflow() {
        String userA = faker.name().username();
        String userB = faker.name().username();
        String password = "Password123!";
        String todoDescription = "Decline task for " + userB;
        String declineReason = "Too busy right now";

        registerUser(userA, faker.internet().emailAddress(), password);
        registerUser(userB, faker.internet().emailAddress(), password);

        LoginPage loginPage = new LoginPage(driver);
        TodoListPage todoListPage = loginPage.loginAs(userA, password);
        AddTodoPage addTodoPage = todoListPage.clickAddTodo();
        todoListPage = addTodoPage.addNewTodo(todoDescription, LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        assignTodoToUser(todoListPage, todoDescription, userB);
        todoListPage.logout();

        todoListPage = loginPage.loginAs(userB, password);
        declineAssignment(todoListPage, todoDescription, declineReason);
        todoListPage.logout();

        todoListPage = loginPage.loginAs(userA, password);
        verifyAssignmentDeclinedNotification(todoListPage, userB, todoDescription, declineReason);
    }

    @Step("Register user: {username}")
    private void registerUser(String username, String email, String password) {
        // Ensure we are on login page
        driver.get(com.example.todo.ui.config.ConfigurationManager.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);
        RegisterPage registerPage = loginPage.clickRegister();
        registerPage.register(username, email, password);
        
        // Wait for registration to complete and redirect to login
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='login-button']")));
    }

    @Step("Assign todo '{description}' to user '{username}'")
    private void assignTodoToUser(TodoListPage todoListPage, String description, String username) {
        todoListPage.assignTodo(description, username);
    }

    @Step("Verify notification and accept assignment for '{description}' with date {date}")
    private void verifyNotificationAndAccept(TodoListPage todoListPage, String description, String date) {
        // Wait for notification to appear (backend may take a moment)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        // Wait until the specific notification is present. 
        wait.until(d -> todoListPage.hasNotification("assigned you a new todo"));
        
        Assert.assertTrue(todoListPage.hasNotification("assigned you a new todo"), "Notification should mention new assignment");
        todoListPage.acceptAssignment(description, date);
    }

    @Step("Decline assignment for '{description}' with reason: {reason}")
    private void declineAssignment(TodoListPage todoListPage, String description, String reason) {
        todoListPage.declineAssignment(description, reason);
    }

    @Step("Verify assignment accepted notification from {username} for {description}")
    private void verifyAssignmentAcceptedNotification(TodoListPage todoListPage, String username, String description) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> todoListPage.hasNotification(username + " accepted your assignment"));
        Assert.assertTrue(todoListPage.hasNotification(username + " accepted your assignment"), "Notification should mention acceptance by " + username);
    }

    @Step("Verify assignment declined notification from {username} for {description}")
    private void verifyAssignmentDeclinedNotification(TodoListPage todoListPage, String username, String description, String reason) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        // Check for "User X declined"
        wait.until(d -> todoListPage.hasNotification(username + " declined your assignment"));
        Assert.assertTrue(todoListPage.hasNotification(username + " declined your assignment"), "Notification should mention declination by " + username);
    }
}
