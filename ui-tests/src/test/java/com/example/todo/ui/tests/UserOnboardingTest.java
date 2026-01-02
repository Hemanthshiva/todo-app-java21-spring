package com.example.todo.ui.tests;

import com.example.todo.ui.base.BaseTest;
import com.example.todo.ui.pages.LoginPage;
import com.example.todo.ui.pages.RegisterPage;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

@Feature("User Onboarding")
public class UserOnboardingTest extends BaseTest {
    private Faker faker = new Faker();

    @Test
    @Description("Verify that a new user can register and login successfully")
    public void shouldAllowNewUserToRegisterAndLoginSuccessfully() {
        String username = faker.name().username();
        String password = "Password123!";
        String email = faker.internet().emailAddress();

        LoginPage loginPage = new LoginPage(driver);
        RegisterPage registerPage = loginPage.clickRegister();
        registerPage.register(username, email, password);

        // Wait for registration to complete and redirect to login
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='username-input']")));
        loginPage = new LoginPage(driver);

        // After registration, it should redirect to login
        loginPage.loginAs(username, password);

        // Verify we're on the todo list page
        Assert.assertTrue(driver.findElements(By.cssSelector("[data-testid='todo-add-button']")).size() > 0, 
            "User should be on todo list page (add-todo button should be visible)");
    }

    @Test
    @Description("Verify that an error is displayed for invalid login credentials")
    public void shouldDisplayErrorForInvalidLoginCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("invalidUser");
        loginPage.enterPassword("invalidPassword");
        loginPage.clickLogin();
        
        // For invalid credentials, the error message should appear on the login page
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
    }

    @Test
    @Description("Verify that registration fails with existing username")
    public void shouldFailToRegisterWithExistingUsername() {
        String username = faker.name().username();
        String password = "Password123!";
        String email = faker.internet().emailAddress();

        // Register first time
        LoginPage loginPage = new LoginPage(driver);
        RegisterPage registerPage = loginPage.clickRegister();
        registerPage.register(username, email, password);
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='username-input']")));

        // Try to register again with same username
        registerPage = loginPage.clickRegister();
        registerPage.register(username, faker.internet().emailAddress(), password);

        // Should see error on registration page (assuming backend validates uniqueness and shows error)
        Assert.assertTrue(driver.getCurrentUrl().contains("/register"), "Should stay on register page on failure");
    }
}
