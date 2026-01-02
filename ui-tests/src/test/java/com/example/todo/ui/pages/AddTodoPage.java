package com.example.todo.ui.pages;

import com.example.todo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AddTodoPage extends BasePage {

    @FindBy(css = "[data-testid='form-description']")
    private WebElement descriptionInput;

    @FindBy(css = "[data-testid='form-targetDate']")
    private WebElement targetDateInput;

    @FindBy(css = "[data-testid='form-status']")
    private WebElement statusCheckbox;

    @FindBy(css = "[data-testid='form-submit-button']")
    private WebElement submitButton;

    public AddTodoPage(WebDriver driver) {
        super(driver);
    }

    public void enterDescription(String description) {
        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    public void enterTargetDate(String date) {
        targetDateInput.clear();
        targetDateInput.sendKeys(date);
    }

    public void setStatus(boolean done) {
        if (statusCheckbox.isSelected() != done) {
            safeClick(statusCheckbox);
        }
    }

    public void markAsCompleted() {
        setStatus(true);
    }

    public TodoListPage clickSubmit() {
        safeClick(submitButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='todo-table']"))); // ensure list loaded
        return new TodoListPage(driver);
    }

    public TodoListPage addNewTodo(String description, String date) {
        enterDescription(description);
        enterTargetDate(date);
        return clickSubmit();
    }
}
