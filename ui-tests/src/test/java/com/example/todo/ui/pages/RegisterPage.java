package com.example.todo.ui.pages;

import com.example.todo.ui.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegisterPage extends BasePage {

    @FindBy(css = "[data-testid='register-username']")
    private WebElement usernameInput;

    @FindBy(css = "[data-testid='register-email']")
    private WebElement emailInput;

    @FindBy(css = "[data-testid='register-password']")
    private WebElement passwordInput;

    @FindBy(css = "[data-testid='register-confirm-password']")
    private WebElement confirmPasswordInput;

    @FindBy(css = "[data-testid='register-submit']")
    private WebElement submitButton;

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public void register(String username, String email, String password) {
        usernameInput.sendKeys(username);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        confirmPasswordInput.sendKeys(password);
        safeClick(submitButton);
    }
}
