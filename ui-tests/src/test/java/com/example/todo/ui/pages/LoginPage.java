package com.example.todo.ui.pages;

import com.example.todo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {

    @FindBy(css = "[data-testid='username-input']")
    private WebElement usernameInput;

    @FindBy(css = "[data-testid='password-input']")
    private WebElement passwordInput;

    @FindBy(css = "[data-testid='login-button']")
    private WebElement loginButton;

    @FindBy(css = ".alert-danger")
    private WebElement errorMessage;

    @FindBy(css = "[data-testid='register-link']")
    private WebElement registerLink;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterUsername(String username) {
        usernameInput.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        safeClick(loginButton);
    }

    public TodoListPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        // After login the app may redirect either to the todo list or to a welcome page.
        // Wait for either the todo-add-button or the welcome-title to appear.
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        // Disable implicit wait to avoid 10s delay if the first element in 'or' is missing
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        try {
            longWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='todo-add-button']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='welcome-title']"))
            ));

            // If we landed on the welcome page, click the "View your todos" button to go to the list
            boolean onWelcomePage = driver.findElements(By.cssSelector("[data-testid='welcome-title']")).size() > 0;
            
            if (onWelcomePage) {
                WebElement viewTodos = driver.findElement(By.cssSelector("[data-testid='todo-view-button']"));
                safeClick(viewTodos);
                longWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='todo-add-button']")));
            }

            Thread.sleep(800); // small buffer to ensure rendering
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("LOGIN FAILED - Current URL: " + driver.getCurrentUrl());
            System.out.println("LOGIN FAILED - Page snippet: " + driver.getPageSource().substring(0, Math.min(500, driver.getPageSource().length())));
            throw e;
        } finally {
            // Restore implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }

        return new TodoListPage(driver);
    }

    public RegisterPage clickRegister() {
        safeClick(registerLink);
        return new RegisterPage(driver);
    }

    public boolean isErrorMessageDisplayed() {
        return isSafelyDisplayed(errorMessage);
    }
}
