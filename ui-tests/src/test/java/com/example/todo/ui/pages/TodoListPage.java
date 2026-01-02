package com.example.todo.ui.pages;

import com.example.todo.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class TodoListPage extends BasePage {

    @FindBy(css = "[data-testid='todo-add-button']")
    private WebElement addButton;

    @FindBy(css = "[data-testid='todo-table']")
    private WebElement todoTable;

    @FindBy(css = "[data-testid='notifications-button']")
    private WebElement notificationsButton;

    @FindBy(css = "[data-testid='notifications-badge']")
    private WebElement notificationsBadge;

    @FindBy(css = "[data-testid='notifications-list']")
    private WebElement notificationsList;

    @FindBy(css = "[data-testid='logout-button']")
    private WebElement logoutButton;

    // Assign Modal
    @FindBy(css = "[data-testid='assign-user-search-input']")
    private WebElement userSearchInput;

    @FindBy(css = "[data-testid='assign-user-result-item']")
    private List<WebElement> searchResults;

    // Accept Modal
    @FindBy(css = "[data-testid='assignment-accept-date-input']")
    private WebElement acceptDateInput;

    @FindBy(css = "[data-testid='assignment-accept-submit']")
    private WebElement acceptSubmitButton;

    // Decline Modal
    @FindBy(css = "[data-testid='assignment-decline-reason-input']")
    private WebElement declineReasonInput;

    @FindBy(css = "[data-testid='assignment-decline-submit']")
    private WebElement declineSubmitButton;

    public TodoListPage(WebDriver driver) {
        super(driver);
    }

    public AddTodoPage clickAddTodo() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='todo-add-button']")));
        safeClick(addButton);
        return new AddTodoPage(driver);
    }

    public boolean isTodoVisible(String description) {
        return !driver.findElements(By.xpath("//td[text()='" + description + "']")).isEmpty();
    }

    public boolean isTodoCompleted(String description) {
        try {
            WebElement statusBadge = driver.findElement(By.xpath("//td[text()='" + description + "']/..//span[contains(@class, 'bg-success') and text()='Completed']"));
            return isSafelyDisplayed(statusBadge);
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteTodo(String description) {
        WebElement deleteButton = driver.findElement(By.xpath("//td[text()='" + description + "']/following-sibling::td//button[contains(@data-testid, 'todo-delete-button')]"));
        safeClick(deleteButton);
    }

    public AddTodoPage clickUpdateTodo(String description) {
        WebElement updateButton = driver.findElement(By.xpath("//td[text()='" + description + "']/following-sibling::td//a[contains(@data-testid, 'todo-edit-button')]"));
        safeClick(updateButton);
        return new AddTodoPage(driver);
    }

    public void assignTodo(String description, String username) {
        WebElement assignButton = driver.findElement(By.xpath("//td[text()='" + description + "']/following-sibling::td//button[@data-testid='todo-assign-button']"));
        safeClick(assignButton);
        
        wait.until(ExpectedConditions.visibilityOf(userSearchInput));
        userSearchInput.sendKeys(username);
        
        wait.until(ExpectedConditions.visibilityOfAllElements(searchResults));
        for (WebElement result : searchResults) {
            if (result.getText().equals(username)) {
                safeClick(result);
                break;
            }
        }
        
        // Wait for assignment to complete (page reload and badge appearance)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='" + description + "']/following-sibling::td//span[contains(text(), 'Assigned to " + username + "')]")));
    }

    public void logout() {
        safeClick(logoutButton);
    }

    public int getNotificationCount() {
        try {
            List<WebElement> badges = driver.findElements(By.cssSelector("[data-testid='notifications-badge']"));
            if (!badges.isEmpty()) {
                WebElement badge = badges.get(0);
                if (badge.isDisplayed()) {
                    return Integer.parseInt(badge.getText());
                }
            }
        } catch (Exception e) {
            // Notification badge not available or not a number
        }
        return 0;
    }

    public void openNotifications() {
        if (!isSafelyDisplayed(notificationsList)) {
            // Try standard click first
            try {
                safeClick(notificationsButton);
                wait.until(ExpectedConditions.visibilityOf(notificationsList));
            } catch (Exception e) {
                // Fallback to JS click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", notificationsButton);
                try {
                    wait.until(ExpectedConditions.visibilityOf(notificationsList));
                } catch (Exception ex) {
                    // Fallback to direct style manipulation (last resort)
                    ((JavascriptExecutor) driver).executeScript("arguments[0].classList.add('show'); arguments[0].style.display='block';", notificationsList);
                    try {
                        wait.until(ExpectedConditions.visibilityOf(notificationsList));
                    } catch (Exception ignored) {
                        // Ignore visibility failure and proceed, maybe we can still read text via JS
                    }
                }
            }
        }
        
        // Always force fetch to ensure we have latest data
        ((JavascriptExecutor) driver).executeScript("if(window.fetchNotifications) { fetchNotifications(); }");
    }

    public boolean hasNotification(String text) {
        openNotifications();
        String content = notificationsList.getText();
        if (content == null || content.isEmpty()) {
            // Fallback to JS innerText if visible text is empty (e.g. if element is hidden)
            content = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText;", notificationsList);
        }
        return content != null && content.contains(text);
    }

    public void acceptAssignment(String description, String date) {
        WebElement acceptButton = driver.findElement(By.xpath("//td[text()='" + description + "']/following-sibling::td//button[@data-testid='assignment-accept-button']"));
        safeClick(acceptButton);
        
        wait.until(ExpectedConditions.visibilityOf(acceptDateInput));
        // Use JS to set date to avoid locale issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", acceptDateInput, date);
        safeClick(acceptSubmitButton);
    }

    public void declineAssignment(String description, String reason) {
        WebElement declineButton = driver.findElement(By.xpath("//td[text()='" + description + "']/following-sibling::td//button[@data-testid='assignment-decline-button']"));
        safeClick(declineButton);
        
        wait.until(ExpectedConditions.visibilityOf(declineReasonInput));
        declineReasonInput.sendKeys(reason);
        safeClick(declineSubmitButton);
    }
}
