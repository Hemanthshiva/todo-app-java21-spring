# API Test Automation Framework

A comprehensive REST API test automation framework for the Todo Application using **RestAssured**, **TestNG**, and **Allure** reporting. This framework provides end-to-end testing of all REST API endpoints with detailed reporting and easy-to-maintain test code.

## Table of Contents

- [Framework Overview](#framework-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Tools & Libraries](#tools--libraries)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Generating Allure Reports](#generating-allure-reports)
- [Test Organization](#test-organization)
- [Writing New Tests](#writing-new-tests)
- [Best Practices](#best-practices)

## Framework Overview

### What is This Framework?

This is a **Behavior-Driven API Testing Framework** that validates the REST API endpoints of the Todo Application. It uses:

- **RestAssured**: Fluent API for making HTTP requests and validating responses
- **TestNG**: Powerful testing framework with assertions and test organization
- **Allure**: Rich reporting with detailed test execution statistics and logs
- **Jackson**: JSON serialization/deserialization for request/response handling

### Key Features

✅ **Fluent REST API Testing**: Easy-to-read HTTP request/response validation  
✅ **Data-Driven Tests**: Parameterized tests for multiple scenarios  
✅ **Comprehensive Reporting**: Allure reports with request/response logs  
✅ **Module Identification**: All tests tagged as `module: API` for easy filtering  
✅ **Reusable Clients**: API client classes for each endpoint group  
✅ **Base Test Setup**: Centralized configuration for authentication and serialization  
✅ **Utilities**: Helper functions for test data generation and assertions  

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Test Framework** | TestNG | 7.9.0 |
| **REST Client** | RestAssured | 5.4.0 |
| **Reporting** | Allure | 2.24.0 |
| **Serialization** | Jackson | 2.17.0 |
| **Build Tool** | Maven | 3.6+ |
| **Java** | OpenJDK | 21 |
| **Browser Driver** | (N/A - API Only) | - |

## Project Structure

```
api-tests/
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
├── src/
│   └── test/
│       ├── java/com/example/todo/api/
│       │   ├── clients/                       # API Client classes
│       │   │   ├── UserClient.java
│       │   │   ├── AuthClient.java
│       │   │   ├── TodoClient.java
│       │   │   ├── AssignmentClient.java
│       │   │   ├── NotificationClient.java
│       │   │   └── BaseClient.java
│       │   │
│       │   ├── models/                        # DTOs and Data Models
│       │   │   ├── UserDto.java
│       │   │   ├── TodoDto.java
│       │   │   ├── TodoAssignmentDto.java
│       │   │   ├── NotificationDTO.java
│       │   │   └── AssignmentStatus.java
│       │   │
│       │   ├── tests/                         # Test Classes
│       │   │   ├── BaseTest.java              # Base test setup with Allure label
│       │   │   ├── UserManagementTest.java    # User CRUD operations
│       │   │   ├── TodoLifecycleTest.java     # Todo CRUD operations
│       │   │   ├── TodoAssignmentWorkflowTest.java  # Assignment workflow
│       │   │   ├── NotificationLifecycleTest.java   # Notification tests
│       │   │   └── SecurityTest.java          # Security & permissions
│       │   │
│       │   └── utils/                         # Utility classes
│       │       └── TestUtils.java             # Helper methods
│       │
│       └── resources/
│           └── testng.xml                     # TestNG configuration
│
└── target/
    └── allure-results/                        # Generated Allure results
```

### Directory Details

#### `clients/` - API Client Classes
Encapsulate HTTP communication with specific endpoint groups:
- **UserClient**: User registration, search, CRUD operations
- **AuthClient**: Authentication and token generation
- **TodoClient**: Todo creation, retrieval, update, deletion
- **AssignmentClient**: Todo assignment workflow
- **NotificationClient**: Notification management
- **BaseClient**: Common HTTP methods and configuration

#### `models/` - Data Transfer Objects (DTOs)
Java POJOs representing API request/response payloads:
- Used for JSON serialization/deserialization
- Enable type-safe test data creation
- Match backend API contracts

#### `tests/` - Test Classes
Organized by feature/functionality:
- **UserManagementTest**: User registration, search, profile management
- **TodoLifecycleTest**: Create, read, update, delete todos
- **TodoAssignmentWorkflowTest**: Full assignment workflow (assign → respond → complete)
- **NotificationLifecycleTest**: Notification creation, reading, lifecycle
- **SecurityTest**: Authentication, authorization, permission validation

#### `utils/` - Helper Utilities
**TestUtils.java**:
- `generateRandomUsername()`: Create unique usernames
- `generateRandomEmail()`: Create unique email addresses
- `extractTodoIdFromHtml()`: Parse HTML responses
- Custom assertions and data validation

## Tools & Libraries

### Core Dependencies

#### RestAssured
**Purpose**: Fluent API for HTTP testing  
**Usage**: Making GET, POST, PUT, DELETE requests with validation

```java
given()
    .auth().basic(username, password)
    .contentType(ContentType.JSON)
    .body(todoDto)
.when()
    .post("/api/todos")
.then()
    .statusCode(201)
    .body("description", equalTo(todoDto.getDescription()));
```

#### TestNG
**Purpose**: Test framework and test execution  
**Features**:
- `@Test`: Mark test methods
- `@BeforeClass`, `@AfterClass`: Test lifecycle hooks
- `@DataProvider`: Parameterized test data
- Assertions and soft assertions for flexible validation

#### Allure
**Purpose**: Rich HTML test reports  
**Usage**: Automatic capture of test metadata, steps, and attachments

```java
@Epic("User Management")
@Feature("User Registration")
@Story("User Registration with Valid Credentials")
@Test(description = "Verify user can register with valid data")
public void testUserRegistration() { ... }
```

#### Jackson
**Purpose**: JSON serialization and deserialization  
**Usage**: Convert Java objects to/from JSON for API payloads

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new JavaTimeModule());
UserDto user = objectMapper.readValue(jsonString, UserDto.class);
```

## Getting Started

### Prerequisites

- **Java 21** (JDK) installed
- **Maven 3.6+** installed
- **Backend Application** running on `http://localhost:8091`
- **Allure CLI** (optional, for local report viewing)

### Setup

1. **Navigate to api-tests directory**:
   ```bash
   cd api-tests
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

## Running Tests

### Run All API Tests

```bash
mvn test -pl api-tests
```

### Run Specific Test Class

```bash
mvn test -pl api-tests -Dtest=UserManagementTest
```

### Run Specific Test Method

```bash
mvn test -pl api-tests -Dtest=UserManagementTest#testUserRegistrationWithValidData
```

### Run Tests with Specific Tags (Feature)

```bash
mvn test -pl api-tests -Dtest=*LifecycleTest
```

### Run Tests and Skip Report Generation

```bash
mvn test -pl api-tests -Dskip.allure.report=true
```

### Run Tests in Parallel

Add to `testng.xml`:
```xml
<suite name="API Tests" parallel="methods" thread-count="5">
    ...
</suite>
```

Then run:
```bash
mvn test -pl api-tests
```

## Generating Allure Reports

### Generate Report Locally

After running tests, generate the Allure report:

```bash
# Generate HTML report
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

# Generate combined report
mkdir -p combined-allure-results
cp -r api-tests/target/allure-results/* combined-allure-results/ 2>/dev/null || true
cp -r ui-tests/target/allure-results/* combined-allure-results/ 2>/dev/null || true

# Generate HTML report
allure generate combined-allure-results --clean -o combined-allure-report

# Open report
allure open combined-allure-report
```

### Serve Report on HTTP Server

```bash
# Using Python 3
cd combined-allure-report
python3 -m http.server 8888

# Using Node.js
npx http-server combined-allure-report -p 8888

# Using Java
cd combined-allure-report
python3 -m http.server 8888
```

Then open: `http://localhost:8888`

### CI/CD Report Access

Reports generated in GitHub Actions are available as **artifacts**:
1. Navigate to the GitHub Actions workflow run
2. Scroll to "Artifacts" section
3. Download **allure-report** artifact
4. Extract and open `index.html`

## Test Organization

### Epic, Feature, Story (Allure Annotations)

Tests are organized using Allure annotations for better reporting:

```java
@Epic("Todo Management")
@Feature("Todo Lifecycle")
@Story("User can create a new todo")
@Test(description = "Verify todo creation with valid data")
public void testTodoCreation() { ... }
```

**Available Epics**:
- `User Management` - User registration, search, CRUD
- `Todo Management` - Todo lifecycle, CRUD operations
- `Notifications` - Notification creation and management
- `Security` - Authentication and authorization

### Module Labeling

All API tests are automatically labeled with `module: API` via the `BaseTest.attachModuleLabel()` method. This allows filtering in Allure reports:

```
Filter by Label → module: API (shows only API tests)
```

## Writing New Tests

### Template for New Test Class

```java
package com.example.todo.api.tests;

import com.example.todo.api.clients.*;
import com.example.todo.api.models.*;
import com.example.todo.api.utils.TestUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Feature Name")
@Feature("Feature Group")
public class YourNewTest extends BaseTest {

    private UserClient userClient;
    private AuthClient authClient;

    @BeforeClass
    public void setup() {
        userClient = new UserClient();
        authClient = new AuthClient();
    }

    @Test(description = "Clear test description")
    @Story("Story about what this test does")
    public void testSomething() {
        // Arrange
        String username = TestUtils.generateRandomUsername();
        String password = "password";

        // Act
        userClient.registerUser(new UserDto(username, password, "email@example.com"));
        String token = authClient.getAuthToken(username, password);

        // Assert
        Assert.assertNotNull(token, "Auth token should not be null");
    }
}
```

### Key Points

1. **Extend BaseTest**: Ensures Allure module label is applied
2. **Use Client Classes**: Reuse existing API clients for consistency
3. **Add Allure Annotations**: `@Epic`, `@Feature`, `@Story` for reporting
4. **Follow AAA Pattern**: Arrange → Act → Assert
5. **Use Descriptive Names**: Test method names should describe what is tested
6. **Use TestUtils**: Generate test data, don't hardcode values

## Best Practices

### 1. Test Independence
- Each test should be independent and runnable in any order
- Clean up test data in `@AfterClass` or use unique identifiers

### 2. Assertions
Use fluent assertions for readability:
```java
.then()
    .statusCode(200)
    .body("id", notNullValue())
    .body("description", equalTo(expectedDescription));
```

### 3. Request/Response Logging
RestAssured automatically logs requests/responses to Allure:
```java
given()
    .log().all()  // Log request
.when()
    .post("/api/todos")
.then()
    .log().all()  // Log response
    .statusCode(201);
```

### 4. Data-Driven Tests
Use `@DataProvider` for testing multiple scenarios:
```java
@DataProvider(name = "invalidCredentials")
public Object[][] invalidCredentials() {
    return new Object[][] {
        { "user", "" },
        { "", "password" },
        { "invalid@user", "wrongpass" }
    };
}

@Test(dataProvider = "invalidCredentials")
public void testLoginWithInvalidCredentials(String user, String pass) { ... }
```

### 5. Error Handling
Provide meaningful failure messages:
```java
Assert.assertNotNull(token, "Authentication token should not be null after login");
Assert.assertTrue(notifications.size() > 0, 
    "User should have at least one notification after assignment");
```

### 6. Reusable Methods
Extract common patterns into utility methods:
```java
// In TestUtils
public static UserDto createAndRegisterUser() {
    UserDto user = new UserDto(
        generateRandomUsername(),
        "password",
        generateRandomEmail()
    );
    new UserClient().registerUser(user);
    return user;
}
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

### Issue: Allure Results Not Generated

**Problem**: `target/allure-results/` directory is empty

**Solution**:
```bash
# Ensure listener is configured in pom.xml
# Delete target and rebuild
mvn clean test -pl api-tests
```

### Issue: Report Shows "No Data"

**Problem**: Allure report opens but shows empty results

**Solution**:
1. Verify tests ran: Check Maven output for "Tests run: X"
2. Check results exist: `ls api-tests/target/allure-results/`
3. Regenerate report: `allure generate --clean api-tests/target/allure-results/`

### Issue: Authentication Token Issues

**Problem**: Tests fail with 401 Unauthorized

**Solution**:
```bash
# Verify backend has default users
# Restart backend: kills previous java processes and restart
ps aux | grep java
kill -9 <PID>
java -jar target/todo-app-0.0.1-SNAPSHOT.jar
```

## Additional Resources

- [RestAssured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/doc/)
- [Allure Documentation](https://docs.qameta.io/allure/)
- [Jackson Documentation](https://github.com/FasterXML/jackson)

## Contributing

When adding new API tests:
1. Create new test class extending `BaseTest`
2. Add appropriate `@Epic`, `@Feature`, `@Story` annotations
3. Use existing client classes or create new ones
4. Add description to `@Test` annotation
5. Follow AAA pattern and naming conventions
6. Run locally and verify Allure report generation
7. Commit with clear message describing tests added

---

**Framework Version**: 1.0  
**Last Updated**: January 2026  
**Maintained By**: Hemanth Shiva
