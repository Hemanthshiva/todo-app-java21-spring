package com.example.todo.ui.base;

import com.example.todo.ui.config.ConfigurationManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
            // **CRITICAL: Set a realistic window size for headless mode**
            options.addArguments("--window-size=1920,1080"); 
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu"); 

        driver = new ChromeDriver(options);

        // This is only necessary if you are NOT running headless and want it maximized.
        if (!headless) {
            driver.manage().window().maximize();
        }
       
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(ConfigurationManager.getBaseUrl());

        // Attach module label for Allure reporting
        io.qameta.allure.Allure.getLifecycle().updateTestCase(testResult -> {
            if (testResult.getLabels() == null) {
                testResult.setLabels(new java.util.ArrayList<>());
            }
            testResult.getLabels().add(new io.qameta.allure.model.Label().setName("module").setValue("UI"));
        });
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}