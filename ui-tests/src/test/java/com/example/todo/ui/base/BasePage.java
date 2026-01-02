package com.example.todo.ui.base;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    /**
     * Safe click: waits for element to be clickable, scrolls into view, and clicks
     */
    protected void safeClick(WebElement element) {
        try {
            // Wait for element to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(element));
            
            // Scroll element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            
            // Small delay to ensure overlays clear
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
            
            // Click the element
            element.click();
        } catch (Exception e) {
            // Fallback: try JavaScript click if regular click fails
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected boolean isSafelyDisplayed(WebElement element) {
        try {
            // Check immediately without waiting for implicit/explicit timeout
            // Set implicit wait to 0 temporarily if needed, but since we have the element proxy,
            // calling isDisplayed should just check visibility.
            // However, if element is not found in DOM, Proxy throws NoSuchElementException.
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
