package com.example.todo.ui.tests;

import com.example.todo.ui.base.BaseTest;
import com.example.todo.ui.pages.AddTodoPage;
import com.example.todo.ui.pages.LoginPage;
import com.example.todo.ui.pages.RegisterPage;
import com.example.todo.ui.pages.TodoListPage;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Feature("Todo Management")
public class TodoManagementTest extends BaseTest {
    private Faker faker = new Faker();
    private String username;
    private String password;

    @BeforeMethod
    public void registerAndLogin() {
        username = faker.name().username();
        password = "Password123!";
        String email = faker.internet().emailAddress();

        LoginPage loginPage = new LoginPage(driver);
        RegisterPage registerPage = loginPage.clickRegister();
        registerPage.register(username, email, password);
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='username-input']")));
        
        loginPage.loginAs(username, password);
    }

    @Test
    @Description("Verify that a logged-in user can add a new todo")
    public void shouldAllowLoggedInUserToAddANewTodo() {
        String description = faker.lorem().sentence();
        String date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        TodoListPage todoListPage = new TodoListPage(driver);
        AddTodoPage addTodoPage = todoListPage.clickAddTodo();
        todoListPage = addTodoPage.addNewTodo(description, date);

        Assert.assertTrue(todoListPage.isTodoVisible(description), "New todo should be visible in the list");
    }

    @Test
    @Description("Verify that a logged-in user can update an existing todo")
    public void shouldAllowLoggedInUserToUpdateAnExistingTodo() {
        String description = faker.lorem().sentence();
        String updatedDescription = "Updated: " + description;
        String date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        TodoListPage todoListPage = new TodoListPage(driver);
        AddTodoPage addTodoPage = todoListPage.clickAddTodo();
        todoListPage = addTodoPage.addNewTodo(description, date);

        addTodoPage = todoListPage.clickUpdateTodo(description);
        addTodoPage.enterDescription(updatedDescription);
        todoListPage = addTodoPage.clickSubmit();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[text()='" + updatedDescription + "']")));
        Assert.assertTrue(todoListPage.isTodoVisible(updatedDescription), "Updated todo should be visible in the list");
    }

    @Test
    @Description("Verify that a logged-in user can delete a todo")
    public void shouldAllowLoggedInUserToDeleteATodo() {
        String description = faker.lorem().sentence();
        String date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        TodoListPage todoListPage = new TodoListPage(driver);
        AddTodoPage addTodoPage = todoListPage.clickAddTodo();
        todoListPage = addTodoPage.addNewTodo(description, date);

        todoListPage.deleteTodo(description);
        // Wait for deletion (page reload or DOM update)
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        Assert.assertFalse(todoListPage.isTodoVisible(description), "Deleted todo should not be visible in the list");
    }

    @Test
    @Description("Verify that a logged-in user can mark a todo as completed")
    public void shouldAllowLoggedInUserToMarkTodoAsCompleted() {
        String description = faker.lorem().sentence();
        String date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        TodoListPage todoListPage = new TodoListPage(driver);
        AddTodoPage addTodoPage = todoListPage.clickAddTodo();
        todoListPage = addTodoPage.addNewTodo(description, date);

        addTodoPage = todoListPage.clickUpdateTodo(description);
        addTodoPage.markAsCompleted();
        todoListPage = addTodoPage.clickSubmit();

        Assert.assertTrue(todoListPage.isTodoCompleted(description), "Todo should be marked as completed");
    }
}
