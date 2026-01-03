# UI Test Automation Framework

A comprehensive browser-based UI test automation framework for the Todo Application using **Selenium WebDriver**, **Page Object Model (POM)**, **TestNG**, and **Allure** reporting. This framework provides end-to-end testing of user workflows with maintainable, scalable test code.

## Table of Contents

- [Framework Overview](#framework-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Tools & Libraries](#tools--libraries)
- [Page Object Model](#page-object-model)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Headless vs Headed Mode](#headless-vs-headed-mode)
- [Generating Allure Reports](#generating-allure-reports)
- [Test Organization](#test-organization)
- [Writing New Tests](#writing-new-tests)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Framework Overview

### What is This Framework?

This is a **Behavior-Driven UI Testing Framework** that automates browser-based testing of the Todo Application. It uses:

- **Selenium WebDriver 4**: Industry-standard browser automation library
- **Page Object Model (POM)**: Design pattern for maintainable test code
- **TestNG**: Powerful testing framework with assertions and test organization
- **Allure**: Rich reporting with screenshots, videos, and detailed logs
- **WebDriverManager**: Automatic browser driver management

### Key Features

✅ **Page Object Model**: Separates test logic from page interaction logic  
✅ **Cross-browser Testing**: Support for Chrome/Chromium browsers  
✅ **Headless & Headed Modes**: Run tests headless in CI or headed locally for debugging  
✅ **Robust Waits**: Custom wait utilities for reliable element detection  
✅ **Screenshot on Failure**: Automatic failure screenshots attached to reports  
✅ **Module Identification**: All tests tagged as `module: UI` for easy filtering  
✅ **Reusable Components**: Base test class with common setup and utilities  
✅ **Comprehensive Reporting**: Allure reports with test steps and logs  

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Test Framework** | TestNG | 7.9.0 |
| **Browser Automation** | Selenium WebDriver | 4.18.1 |
| **Driver Management** | WebDriverManager | 5.7.0 |
| **Reporting** | Allure | 2.20.1 |
| **Build Tool** | Maven | 3.6+ |
| **Java** | OpenJDK | 21 |
| **Browsers** | Chrome/Chromium | Latest |

## Project Structure

```
ui-tests/
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
├── src/
│   └── test/
│       ├── java/com/example/todo/ui/
│       │   ├── base/                          # Base test class and utilities
│       │   │   └── BaseTest.java              # Base setup with driver, waits, module label
│       │   │
│       │   ├── pages/                         # Page Object Classes (POM)
│       │   │   ├── BasePage.java              # Base page with common methods
│       │   │   ├── LoginPage.java             # Login page interactions
│       │   │   ├── RegisterPage.java          # Registration page
│       │   │   ├── TodoListPage.java          # Todo list view page
│       │   │   ├── AddTodoPage.java           # Add todo form page
│       │   │   └── WelcomePage.java           # Welcome/redirect page
│       │   │
│       │   ├── tests/                         # Test Classes
│       │   │   ├── UserOnboardingTest.java    # Registration, login, logout
│       │   │   ├── TodoManagementTest.java    # CRUD operations
│       │   │   └── TodoAssignmentWorkflowTest.java  # Assignment workflows
│       │   │
│       │   ├── config/                        # Configuration classes
│       │   │   └── ConfigurationManager.java  # Base URL and config
│       │   │
│       │   ├── utils/                         # Utility classes
│       │   │   ├── ScreenshotUtils.java       # Screenshot capture
│       │   │   └── DateUtils.java             # Date formatting utilities
│       │   │
│       │   └── support/                       # Test support classes
│       │       └── TestDataBuilder.java       # Test data builders
│       │
│       └── resources/
│           ├── testng.xml                     # TestNG configuration
│           └── config.properties              # Test configuration
│
└── target/
    └── allure-results/                        # Generated Allure results
```

### Directory Details

#### `pages/` - Page Object Classes
Encapsulate all UI interactions for each page:
- **LoginPage**: Login form interactions and assertions
- **RegisterPage**: User registration form
- **TodoListPage**: Todo list view, filters, notifications
- **AddTodoPage**: Add/edit todo form
- **WelcomePage**: Welcome/redirect page after login
- **BasePage**: Common methods shared across pages

#### `tests/` - Test Classes
Organized by user workflows:
- **UserOnboardingTest**: Registration, login, logout flows
- **TodoManagementTest**: Create, update, delete, mark complete
- **TodoAssignmentWorkflowTest**: Complete assignment workflow (assign → respond → complete)

#### `base/` - Base Test Class
**BaseTest.java**:
- WebDriver initialization with Chrome options
- Headless/headed mode toggle
- WebDriverWait setup
- Module label attachment for Allure
- Setup and teardown lifecycle

#### `config/` - Configuration
**ConfigurationManager.java**:
- Base URL management
- Browser configuration
- Timeout settings

#### `utils/` - Utilities
- **ScreenshotUtils**: Attach screenshots to Allure reports
- **DateUtils**: Date formatting (dd/MM/yyyy format for UI)

## Tools & Libraries

### Core Dependencies

#### Selenium WebDriver
**Purpose**: Browser automation and element interaction  
**Usage**: Navigate pages, click buttons, fill forms, validate content

```java
driver.findElement(By.id("username")).sendKeys("testuser");
driver.findElement(By.cssSelector("button[type='submit']")).click();
```

#### WebDriverManager
**Purpose**: Automatic browser driver management  
**Usage**: Eliminates need to manually download/manage ChromeDriver

```java
WebDriverManager.chromedriver().setup();
driver = new ChromeDriver(options);
```

#### Page Object Model (POM)
**Purpose**: Design pattern for maintainable test code  
**Benefits**:
- Centralized page element locators
- Reusable page methods
- Easy maintenance when UI changes
- Clear separation of concerns

#### TestNG
**Purpose**: Test framework and test execution  
**Features**:
- `@Test`: Mark test methods
- `@BeforeMethod`, `@AfterMethod`: Test lifecycle hooks
- `@DataProvider`: Parameterized test data
- Assertions for test validation

#### Allure
**Purpose**: Rich HTML test reports  
**Usage**: Automatic capture of test metadata, steps, and screenshots

```java
@Feature("User Management")
@Story("User can register and login")
@Test(description = "Register new user")
public void testUserRegistration() { ... }
```

#### WebDriverWait
**Purpose**: Explicit waits for element presence/visibility  
**Usage**: Reliably detect when elements are ready for interaction

```java
wait.until(ExpectedConditions.presenceOfElementLocated(By.id("todo-add-btn")));
```

### Browser Options

The framework supports:
- **Headless Mode** (default for CI): `--headless=new`
- **Window Size**: `--window-size=1920,1080`
- **Sandbox Disabled**: `--no-sandbox` (for CI environments)
- **GPU Disabled**: `--disable-gpu`

## Page Object Model

### What is POM?

The **Page Object Model** is a design pattern that:
- Represents each page/component as a Java class
- Encapsulates all page elements and interactions
- Separates test logic from UI implementation details
- Makes tests more maintainable and readable

### Example Page Object

```java
public class LoginPage extends BasePage {
    
    // Page elements (locators)
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    private By submitButton = By.cssSelector("button[type='submit']");
    private By errorMessage = By.cssSelector(".error-message");
    
    // Constructor
    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }
    
    // Page methods
    public void enterUsername(String username) {
        safeClick(driver.findElement(usernameInput));
        driver.findElement(usernameInput).sendKeys(username);
    }
    
    public void enterPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }
    
    public TodoListPage clickLogin() {
        safeClick(driver.findElement(submitButton));
        return new TodoListPage(driver, wait);
    }
    
    public LoginPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }
    
    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }
}
```

### Benefits

| Benefit | Description |
|---------|-------------|
| **Maintainability** | Change locators in one place; tests remain unchanged |
| **Readability** | `loginPage.loginAs("user", "pass")` is self-documenting |
| **Reusability** | Methods can be used across multiple tests |
| **Abstraction** | Tests focus on *what* not *how* |
| **Scalability** | Easy to add new pages and tests |

## Getting Started

### Prerequisites

- **Java 21** (JDK 21 or later)
- **Maven 3.6+**
- **Chrome/Chromium Browser** installed
- **Backend Application** running on `http://localhost:8091`
- **Allure CLI** (optional, for local report viewing)

### Setup

1. **Navigate to ui-tests directory**:
   ```bash
   cd ui-tests
   ```

2. **Install dependencies**:
   ```bash
   mvn clean install
   ```

3. **Verify backend is running**:
   ```bash
   curl http://localhost:8091/login
   ```
   You should see the login HTML page.

4. **Verify Chrome is installed**:
   ```bash
   which google-chrome  # Linux
   /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --version  # macOS
   ```

## Running Tests

### Run All UI Tests (Headless - Default)

```bash
mvn test -pl ui-tests
```

### Run UI Tests in Headed Mode (Local Debugging)

Run tests with visible browser window:

```bash
mvn test -pl ui-tests -Dheadless=false
```

Use this for:
- Visual debugging of failures
- Understanding test behavior
- Developing new tests
- Capturing manual test videos

### Run Specific Test Class

```bash
mvn test -pl ui-tests -Dtest=UserOnboardingTest
```

### Run Specific Test Method

```bash
mvn test -pl ui-tests -Dtest=UserOnboardingTest#shouldAllowNewUserToRegisterAndLoginSuccessfully
```

### Run Tests with Pattern

```bash
mvn test -pl ui-tests -Dtest=*AssignmentWorkflowTest
```

### Run Only Passing Tests (Skip Known Failures)

In `testng.xml`, update suite:
```xml
<suite name="UI Tests" parallel="false">
    <test name="Onboarding">
        <classes>
            <class name="com.example.todo.ui.tests.UserOnboardingTest" />
        </classes>
    </test>
</suite>
```

Then run:
```bash
mvn test -pl ui-tests
```

## Headless vs Headed Mode

### Headless Mode (Default)

**What**: Tests run without displaying the browser window  
**When**: CI/CD pipelines, automated test runs  
**Speed**: ~30% faster  
**Resources**: Less memory and CPU  
**Debugging**: Harder to visualize what's happening

```bash
mvn test -pl ui-tests
# Or explicitly:
mvn test -pl ui-tests -Dheadless=true
```

### Headed Mode

**What**: Tests run with visible browser window  
**When**: Local development, debugging failures  
**Speed**: Slower (rendering overhead)  
**Resources**: More memory and CPU  
**Debugging**: Easy to see test execution in real-time

```bash
mvn test -pl ui-tests -Dheadless=false
```

### Switching Between Modes

The framework dynamically switches based on system property:

```java
// In BaseTest.setUp()
boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
if (headless) {
    options.addArguments("--headless=new");
}
```

### When to Use Each

| Scenario | Mode | Reason |
|----------|------|--------|
| GitHub Actions CI | Headless | No display server available |
| Local test run | Headed | Visual debugging |
| Develop new test | Headed | See what's happening |
| Quick validation | Headless | Faster execution |
| Investigate failure | Headed | Understand user interaction |

## Generating Allure Reports

### Generate Report Locally

After running tests, generate the Allure report:

```bash
# Generate HTML report from UI test results
allure generate target/allure-results --clean -o target/allure-report

# Open in browser (macOS/Linux)
allure open target/allure-report

# Or manually open
open target/allure-report/index.html
```

### Generate Combined Report (All Modules)

From the project root:

```bash
# Install Allure (if not already installed)
curl -Lo allure-2.20.1.tgz https://github.com/allure-framework/allure2/releases/download/2.20.1/allure-2.20.1.tgz
tar -zxf allure-2.20.1.tgz
export PATH="$(pwd)/allure-2.20.1/bin:$PATH"

# Collect results from all modules
mkdir -p combined-allure-results
cp -r api-tests/target/allure-results/* combined-allure-results/ 2>/dev/null || true
cp -r ui-tests/target/allure-results/* combined-allure-results/ 2>/dev/null || true

# Generate combined HTML report
allure generate combined-allure-results --clean -o combined-allure-report

# Open report
allure open combined-allure-report
```

### Serve Report on HTTP Server

Once generated, serve the report on a local HTTP server:

```bash
# Using Python 3
cd combined-allure-report
python3 -m http.server 8888

# Using Node.js (if installed)
npx http-server combined-allure-report -p 8888

# Using Java
cd combined-allure-report
python3 -m http.server 8888
```

Then open: `http://localhost:8888` in your browser

### CI/CD Report Access

Reports generated in GitHub Actions are available as **artifacts**:
1. Navigate to the GitHub Actions workflow run
2. Scroll to "Artifacts" section
3. Download **allure-report** artifact
4. Extract and open `index.html` in browser

### Allure Report Features

The generated report includes:

- **Overview**: Pass/fail statistics, duration, timeline
- **Test Suites**: Organized test results by class
- **Test Cases**: Detailed test execution with steps
- **Attachments**: Screenshots on failure, logs, assertions
- **Labels**: Filter by `module: UI`, `Feature`, `Story`
- **Timeline**: Visual execution timeline
- **Categories**: Defects, failures, skipped tests

## Test Organization

### Epic, Feature, Story (Allure Annotations)

Tests are organized using Allure annotations:

```java
@Feature("User Onboarding")
@Story("User can register and login")
@Test(description = "Register new user with valid credentials")
public void testUserRegistration() { ... }
```

**Available Features**:
- `User Onboarding` - Registration, login, logout
- `Todo Management` - CRUD operations
- `Todo Assignment` - Assignment workflow (assign, respond, complete)
- `Notifications` - Notification display and management

### Module Labeling

All UI tests are automatically labeled with `module: UI` via `BaseTest.attachModuleLabel()`. This allows filtering in Allure reports:

```
Filter by Label → module: UI (shows only UI tests)
```

Use this to distinguish UI tests from API tests in combined reports.

## Writing New Tests

### Template for New Test Class

```java
package com.example.todo.ui.tests;

import com.example.todo.ui.base.BaseTest;
import com.example.todo.ui.pages.*;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Feature Name")
public class YourNewTest extends BaseTest {

    @Test(description = "Clear description of what test does")
    @Story("Story about the feature")
    public void testSomething() {
        // Arrange - Set up test data and initial state
        LoginPage loginPage = new LoginPage(driver, wait);
        
        // Act - Perform user actions
        TodoListPage todoListPage = loginPage.loginAs("user1", "user123");
        
        // Assert - Validate results
        Assert.assertTrue(todoListPage.isTodoAddButtonDisplayed(), 
            "Add todo button should be visible after login");
    }
}
```

### Creating a New Page Object

```java
package com.example.todo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class YourNewPage extends BasePage {
    
    // Locators
    private By titleElement = By.css("h1.page-title");
    private By actionButton = By.id("action-btn");
    
    // Constructor
    public YourNewPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }
    
    // Page methods
    public String getPageTitle() {
        return driver.findElement(titleElement).getText();
    }
    
    public void clickAction() {
        safeClick(driver.findElement(actionButton));
    }
    
    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(titleElement)) != null;
    }
}
```

### Key Points

1. **Extend BaseTest or BasePage**: Reuse common functionality
2. **Use Page Objects**: Don't put test logic and UI code in same method
3. **Add Allure Annotations**: `@Feature`, `@Story` for better reporting
4. **Follow AAA Pattern**: Arrange → Act → Assert
5. **Use Descriptive Names**: Method names should describe action
6. **Wait for Elements**: Always use explicit waits, not Thread.sleep()
7. **Handle Waits**: Use `safeClick()` and `waitForPageLoad()` from BasePage

## Best Practices

### 1. Use Explicit Waits, Not Thread.sleep()

❌ **Bad**:
```java
Thread.sleep(5000);
driver.findElement(By.id("element")).click();
```

✅ **Good**:
```java
wait.until(ExpectedConditions.elementToBeClickable(By.id("element"))).click();
```

### 2. Page Object for Every Page/Component

✅ Create page objects for:
- Login page
- List pages
- Form pages
- Modal dialogs
- Notification panels

### 3. Reusable Methods in Base Classes

✅ Put common methods in `BaseTest` or `BasePage`:
```java
public void safeClick(WebElement element) {
    wait.until(ExpectedConditions.elementToBeClickable(element));
    element.click();
}
```

### 4. Descriptive Test Names

✅ **Good**:
```java
testUserCanLoginWithValidCredentials()
shouldDisplayErrorMessageForInvalidEmail()
shouldAssignTodoAndReceiveNotification()
```

❌ **Bad**:
```java
test1()
loginTest()
doSomething()
```

### 5. Organize Test Data

✅ Use `@DataProvider` for multiple scenarios:
```java
@DataProvider(name = "validUserData")
public Object[][] validUserData() {
    return new Object[][] {
        { "user1", "password123", "user1@example.com" },
        { "testuser", "pass456", "test@example.com" }
    };
}

@Test(dataProvider = "validUserData")
public void testRegistrationWithMultipleUsers(String user, String pass, String email) { ... }
```

### 6. Screenshot on Failure

Allure automatically captures screenshots on failure. Ensure `ScreenshotUtils` is used:

```java
@Attachment(value = "Page screenshot", type = "image/png")
public byte[] takeScreenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
}
```

### 7. Clear Assertions with Messages

```java
Assert.assertTrue(todoListPage.isTodoVisible(todoName), 
    "Todo '" + todoName + "' should be visible in the list");
```

## Troubleshooting

### Issue: Tests Fail with Connection Error

**Problem**: `Connection refused to http://localhost:8091`

**Solution**:
```bash
# Start backend application
cd ../todo-app
java -jar target/todo-app-0.0.1-SNAPSHOT.jar
```

### Issue: ChromeDriver Not Found

**Problem**: `IllegalStateException: chromedriver is not available`

**Solution**:
```bash
# WebDriverManager should auto-download, but if it fails:
mvn clean install
# Or manually install Chrome
```

### Issue: Elements Not Found / TimeoutException

**Problem**: `TimeoutException: Expected condition failed: presence of element`

**Solution**:
1. Run test in headed mode to see what's happening: `mvn test -Dheadless=false`
2. Increase wait timeout in `BaseTest`:
   ```java
   wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Increase from 15
   ```
3. Check locators in page object are correct
4. Ensure backend is running and page loads

### Issue: Allure Results Not Generated

**Problem**: `target/allure-results/` directory is empty

**Solution**:
```bash
# Ensure listener is configured in pom.xml
# Delete target and rebuild
mvn clean test -pl ui-tests

# Verify results were created
ls -la target/allure-results/
```

### Issue: Report Shows Empty/No Data

**Problem**: Allure report opens but shows no test results

**Solution**:
1. Verify tests ran: Check Maven output for "Tests run: X"
2. Check results exist: `ls ui-tests/target/allure-results/`
3. Regenerate report: `allure generate --clean ui-tests/target/allure-results/`
4. Ensure you have result files: `*.json` files should exist

### Issue: Headed Mode Not Working

**Problem**: Tests run headless even with `-Dheadless=false`

**Solution**:
```bash
# Ensure property is passed correctly
mvn test -pl ui-tests -Dheadless=false

# Verify in BaseTest it's being read
mvn test -pl ui-tests -Dheadless=false -e  # Enable debug output
```

### Issue: Date Format Errors

**Problem**: "Invalid date format" or "Expected dd/MM/yyyy"

**Solution**:
The UI expects dates in **dd/MM/yyyy** format:
```java
// ✅ Correct
String date = "25/12/2024";  // day/month/year

// ❌ Wrong
String date = "2024-12-25";  // year-month-day
String date = "12/25/2024";  // month/day/year
```

### Issue: Notification Dropdown Not Showing

**Problem**: Tests can't find notifications in dropdown

**Solution**:
1. Ensure notification icon is clicked before checking contents
2. Add explicit wait for notification list population:
   ```java
   wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='notification-list']")));
   ```
3. Increase wait time if backend is slow creating notifications

## Additional Resources

- [Selenium Documentation](https://selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/doc/)
- [Allure Documentation](https://docs.qameta.io/allure/)
- [Page Object Model Guide](https://selenium.dev/documentation/test_practices/encouraged/page_object_models/)
- [WebDriverWait and Expected Conditions](https://selenium.dev/documentation/webdriver/waits/)

## Contributing

When adding new UI tests:
1. Create new page object class extending `BasePage`
2. Encapsulate all page interactions in the page object
3. Create test class extending `BaseTest`
4. Add appropriate `@Feature`, `@Story` annotations
5. Follow AAA pattern and naming conventions
6. Test in both headless and headed modes locally
7. Verify Allure report generation
8. Commit with clear message describing tests added

---

**Framework Version**: 1.0  
**Last Updated**: January 2026  
**Maintained By**: Hemanth Shiva
**Browser Support**: Chrome/Chromium 4.18.1+
