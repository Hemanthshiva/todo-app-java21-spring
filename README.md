# Todo Application - Collaborative Task Management System

## Project Overview

This is a modern **Spring Boot 3.2.5** application built with **Java 21** that provides a comprehensive collaborative task management and todo tracking system. It features user authentication, task assignment workflows, and real-time notifications, enabling teams to efficiently manage and collaborate on tasks.

The application is designed with a complete tech stack including:
- **Backend Framework**: Spring Boot 3.2.5 with Spring Web, Spring Security, and Spring Data JPA
- **Frontend**: Thymeleaf templating with Bootstrap 5.3.3 for responsive UI
- **Database**: H2 (in-memory) for development, SQLite for production/Docker deployments
- **Documentation**: Swagger/OpenAPI 3.0 with interactive Swagger UI
- **Build Tool**: Apache Maven
- **Containerization**: Docker and Docker Compose support

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [User Workflow: Assigning Todos](#user-workflow-assigning-todos)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Endpoints Reference](#endpoints-reference)
- [Docker Support](#docker-support)
- [Technology Stack](#technology-stack)
- [Development](#development)
- [Testing](#testing)

## Features

### Core Todo Management
- **Create Todos**: Users can create new tasks with descriptions and target completion dates
- **View Todos**: Display all todos for the logged-in user with filtering and sorting
- **Update Todos**: Modify todo descriptions, dates, and completion status
- **Delete Todos**: Remove unwanted todos from the system
- **Mark Complete**: Toggle todo status between pending and completed

### Collaborative Features
- **Assign Todos**: Create and manage task assignments to other users
- **Assignment Workflow**: Three-step workflow - Assign → Accept/Decline → Complete
- **Task Transfer**: Users can take ownership of assigned tasks
- **Permission Management**: Only task owners and assigned users can edit tasks

### User Management
- **User Registration**: Create new user accounts with email validation
- **Secure Authentication**: Form-based authentication with Spring Security
- **Password Encryption**: Passwords are securely hashed using bcrypt
- **User Search**: Search functionality to find users for task assignment

### Notifications
- **Real-time Alerts**: Get notified about task assignments and updates
- **Notification Management**: Mark notifications as read
- **Event Tracking**: Track assignment acceptance, decline, and completion events

### API Documentation
- **Swagger UI**: Interactive API documentation and testing interface
- **OpenAPI 3.0**: Complete API specification
- **API Info Page**: User-friendly overview of available endpoints

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** (JDK 21 or later)
- **Apache Maven 3.6+**
- **Git** (for cloning the repository)
- **Docker & Docker Compose** (optional, for containerized deployment)

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Hemanthshiva/todo-app-java21-spring.git
   cd todo-app
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

   This command:
   - Downloads all dependencies
   - Compiles the source code
   - Runs tests
   - Packages the application

3. **Verify Installation**
   ```bash
   mvn --version
   java -version
   ```

## Running the Application

### Running via Maven

Start the application using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8091**

### Running the Packaged JAR

After building, you can run the JAR file directly:

```bash
java -jar target/todo-app-0.0.1-SNAPSHOT.jar
```

### Accessing the Application

Once the application is running:

1. **Web Interface**: http://localhost:8091
   - Main todo list and management interface
   
2. **Swagger UI**: http://localhost:8091/swagger-ui/index.html
   - Interactive API documentation and testing
   
3. **API Info Page**: http://localhost:8091/api-info
   - Overview of available endpoints
   
4. **Login Page**: http://localhost:8091/login
   - User authentication
   
5. **Registration Page**: http://localhost:8091/register
   - Create new user accounts

## Project Structure

```
todo-app/
├── src/
│   ├── main/
│   │   ├── java/com/learn/spring/todoapp/
│   │   │   ├── config/              # Configuration classes (OpenAPI, Security)
│   │   │   ├── controller/          # MVC and REST controllers
│   │   │   ├── entity/              # JPA entities (Todo, User, Assignment)
│   │   │   ├── repository/          # Spring Data JPA repositories
│   │   │   ├── service/             # Business logic services
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── security/            # Security-related classes
│   │   │   └── TodoAppApplication.java
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf HTML templates
│   │       ├── static/              # CSS, JavaScript, images
│   │       ├── application.properties
│   │       ├── schema.sql           # Database schema initialization
│   │       └── data.sql             # Sample data
│   └── test/
│       ├── java/                    # Unit and integration tests
│       └── resources/               # Test configuration files
├── pom.xml                          # Maven project configuration
├── Dockerfile                       # Docker image definition
├── docker-compose.yml               # Docker Compose configuration
└── README.md                        # This file
```

### Key Components

#### Controllers
- **TodoControllerJpa**: MVC controller for todo management views and CRUD operations
- **UserController**: User registration, login, and search functionality
- **TodoAssignmentController**: REST endpoints for assignment workflow
- **NotificationController**: REST endpoints for notification management
- **WelcomeController**: Home page controller
- **LoginController**: Login page routing

#### Entities
- **Todo**: Represents a task with description, target date, and completion status
- **User**: User account with username, password, and email
- **TodoAssignment**: Assignment relationship between users and todos
- **Notification**: User notifications for events and updates
- **AssignmentStatus**: Enum for assignment states (PENDING, ACCEPTED, DECLINED, COMPLETED)

#### Services
- **TodoAssignmentService**: Core business logic for assignment workflow
- **NotificationService**: Notification creation and retrieval
- **UserInitializer**: Initialization of default users and roles
- **DatabaseUserDetailsService**: Spring Security user details provider

#### Repositories
- **TodoRepository**: JPA repository for todo persistence
- **UserRepository**: User data access layer
- **TodoAssignmentRepository**: Assignment data persistence
- **NotificationRepository**: Notification storage and retrieval
- **AuthorityRepository**: User authority/role management

## User Workflow: Assigning Todos

The application implements a complete collaborative workflow for task assignments:

### Step 1: Create and Assign a Task
1. User logs in and navigates to their todo list
2. Creates a new todo or selects an existing one
3. Clicks the "Assign" button
4. Searches for a user by username
5. Clicks "Assign" next to the desired user
6. Task status changes to "Assigned to [username]" (Pending)

### Step 2: Receive Assignment Notification
1. Assigned user receives a notification (shown via bell icon)
2. Notification appears in the "Assigned to You" section
3. User can see the task in their dashboard with pending status
4. Count of unread notifications is displayed

### Step 3: Respond to Assignment
The assignee has two options:

**Option A: Accept the Assignment**
- Sets a tentative completion date for the task
- Task status changes to "Accepted"
- Original assigner receives a notification
- Assignee can now edit and work on the task

**Option B: Decline the Assignment**
- Provides an optional reason for declining
- Task status reverts to unassigned
- Original assigner receives a notification with the decline reason
- Task is available for assignment to another user

### Step 4: Complete the Task
1. Once accepted, the assignee can update the task details
2. Marks the task as "Done" when completed
3. Original assigner receives a completion notification
4. Task appears in the completed section for both users

## API Documentation

### Swagger UI

Interactive API documentation is available at:

```
http://localhost:8091/swagger-ui/index.html
```

Features:
- Visual representation of all endpoints
- Request/response schemas
- Try-it-out functionality for testing endpoints
- Authentication support for protected endpoints
- Detailed parameter and response documentation

### API Information Page

User-friendly API overview at:

```
http://localhost:8091/api-info
```

Provides:
- Quick start guide
- Available endpoint categories
- Navigation to Swagger UI
- Authentication instructions

## Authentication

### User Credentials

The application uses form-based authentication with Spring Security.

**Default Users** (created on application startup):
- **Username**: `admin` | **Password**: `admin123`
- **Username**: `user1` | **Password**: `user123`
- **Username**: `user2` | **Password**: `user123`

### Authentication URLs

- **Login Page**: http://localhost:8091/login
- **Registration Page**: http://localhost:8091/register
- **Logout**: Use the logout link in the application header

### Authentication Methods

1. **Form-Based Authentication**: Default login form
2. **Basic Authentication**: For API calls (not recommended for production)
3. **Session-Based**: Maintains user session after login

### Security Features

- Passwords are encrypted using bcrypt
- CSRF protection enabled
- Session-based authentication
- Authority-based access control
- Secure password validation during registration

## Endpoints Reference

### Web (MVC) Endpoints

#### Home & Authentication
| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|-----------------|
| `/` | GET | Home page / redirect | Required |
| `/welcome` | GET | Welcome page | Required |
| `/login` | GET | Login form | None |
| `/login` | POST | Process login | None |
| `/register` | GET | Registration form | None |
| `/register` | POST | Create new account | None |
| `/logout` | POST | Logout | Required |

#### Todo Management
| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|-----------------|
| `/list-todos` | GET | List all user todos | Required |
| `/add-todo` | GET | Show create todo form | Required |
| `/add-todo` | POST | Create new todo | Required |
| `/todos/{id}` | GET | Show edit todo form | Required |
| `/todos/{id}` | PUT | Update existing todo | Required |
| `/todos/{id}` | DELETE | Delete todo | Required |

### REST API Endpoints

#### User Management
| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|-----------------|
| `/api/users/search?username={query}` | GET | Search users by username | Required |

#### Todo Assignment Workflow
| Endpoint | Method | Description | Request Body | Authentication |
|----------|--------|-------------|---------------|-----------------|
| `/todos/{todoId}/assign` | POST | Assign todo to user | `{"assigneeUsername": "string"}` | Required |
| `/assignments/{assignmentId}/respond` | POST | Accept/decline assignment | `{"action": "accept\|decline", "tentativeCompletionDate": "date", "declineReason": "string"}` | Required |

#### Notifications
| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|-----------------|
| `/api/notifications` | GET | Get unread notifications | Required |
| `/api/notifications/{id}/read` | POST | Mark notification as read | Required |

### API Information
| Endpoint | Method | Description | Authentication |
|----------|--------|-------------|-----------------|
| `/api-info` | GET | API documentation page | None |
| `/swagger-ui/index.html` | GET | Swagger UI | None |
| `/v3/api-docs` | GET | OpenAPI specification | None |

## Docker Support

This application is fully containerized and can be deployed using Docker and Docker Compose.

### Prerequisites

- Docker installed on your machine
- Docker Compose (for multi-container setup)

### Building the Docker Image

Build the image using the multi-stage Dockerfile:

```bash
docker build -t todo-app .
```

The Dockerfile:
- Uses OpenJDK 21 slim base image
- Builds the application using Maven in the first stage
- Creates an optimized runtime image in the second stage
- Exposes port 8091
- Mounts a volume at `/data` for database persistence

### Running Locally with Docker

#### Standalone Container

Run the application as a single container:

```bash
docker run -d -p 8091:8091 -v todo-data:/data --name todo-app-container todo-app
```

Command options:
- `-d`: Run in detached mode (background)
- `-p 8091:8091`: Map port 8091 from host to container
- `-v todo-data:/data`: Create and mount named volume for data persistence
- `--name todo-app-container`: Name the container for easy reference

Access the application at: http://localhost:8091

#### Viewing Logs

```bash
docker logs -f todo-app-container
```

#### Managing the Container

Stop the container:
```bash
docker stop todo-app-container
```

Start a stopped container:
```bash
docker start todo-app-container
```

Remove the container:
```bash
docker rm todo-app-container
```

### Docker Compose Deployment

For a complete, production-like setup, use Docker Compose:

#### Configuration

The `docker-compose.yml` file defines:
- **todo-app service**: The main application
- **Port mapping**: 8091:8091
- **Persistent volume**: todo-data for SQLite database
- **Restart policy**: Unless stopped

#### Running with Docker Compose

Start the application stack:

```bash
docker-compose up -d
```

Options:
- `-d`: Run in detached mode
- `--build`: Rebuild images before starting

#### Monitoring

View logs:
```bash
docker-compose logs -f todo-app
```

View service status:
```bash
docker-compose ps
```

#### Stopping Services

Stop all services:
```bash
docker-compose down
```

Stop without removing volumes:
```bash
docker-compose down --remove-orphans
```

#### Database Persistence

- SQLite database file: `/data/todo.db` (inside container)
- Persistent volume: `todo-data` (on host machine)
- Data persists across container restarts and removals

#### Environment Configuration

To use environment variables in Docker Compose, create a `.env` file:

```env
APP_PORT=8091
JAVA_OPTS=-Xmx512m
```

## Technology Stack

### Backend
- **Java 21**: Latest Java LTS version with modern language features
- **Spring Boot 3.2.5**: Modern Spring Boot with Servlet API 6 and Jakarta EE
- **Spring Web**: REST and MVC controller support
- **Spring Security**: Authentication and authorization framework
- **Spring Data JPA**: Object-relational mapping and persistence
- **Thymeleaf**: Modern server-side template engine
- **Lombok**: Reduces boilerplate code with annotations

### Frontend
- **Bootstrap 5.3.3**: Responsive CSS framework
- **jQuery 3.6.0**: JavaScript library for DOM manipulation
- **Bootstrap Datepicker 1.9.0**: Date selection widget
- **Thymeleaf Layout Dialect**: Template composition

### Database
- **H2 Database**: In-memory database for development
- **SQLite**: File-based database for production/Docker
- **Hibernate**: JPA implementation for ORM

### API Documentation
- **SpringDoc OpenAPI 2.3.0**: Automatic OpenAPI/Swagger generation
- **Swagger UI**: Interactive API documentation interface

### Testing
- **JUnit 5**: Testing framework
- **Mockito**: Mocking library
- **Spring Security Test**: Security-specific test support

### Build & Deployment
- **Apache Maven 3.6+**: Build automation
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration

## Development

### Building the Project

```bash
# Clean and build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -DskipTests -pl :module-name
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TodoControllerTest

# Run with coverage
mvn clean test jacoco:report
```

### Code Quality

```bash
# Check for common issues
mvn spotbugs:check

# Run code analysis
mvn pmd:check

# Format code
mvn spotless:apply
```

### IDE Setup

#### IntelliJ IDEA
1. Open project with "pom.xml"
2. Maven will auto-download dependencies
3. Set Project SDK to Java 21
4. Mark `src/main/java` as Sources Root
5. Mark `src/test/java` as Test Sources Root

#### Eclipse
1. Import as "Existing Maven Projects"
2. Maven will configure the project
3. Set JRE to Java 21
4. Configure Thymeleaf support if needed

## Testing

The project includes comprehensive test coverage with two specialized test suites:

### Test Modules

#### 1. **API Tests** (`api-tests/`)
End-to-end REST API testing using **RestAssured**, **TestNG**, and **Allure** reporting.

- **Framework**: RestAssured with fluent API for HTTP requests
- **Test Framework**: TestNG with data-driven and parallel execution support
- **Reporting**: Allure with detailed request/response logging
- **Module Label**: All API tests are tagged with `module: API` for easy identification in Allure reports
- **Coverage**: User management, todo lifecycle, assignments, notifications, security

Run API tests:
```bash
mvn test -pl api-tests
```

#### 2. **UI Tests** (`ui-tests/`)
Browser-based UI automation using **Selenium WebDriver** with **Page Object Model** pattern.

- **Framework**: Selenium WebDriver 4.18.1 with Chrome/Chromium support
- **Page Object Model**: Maintainable, reusable page classes for all app pages
- **Test Framework**: TestNG with TestNG listeners for robust test execution
- **Reporting**: Allure with screenshots on failure
- **Module Label**: All UI tests are tagged with `module: UI` for easy identification in Allure reports
- **Headless Mode**: Run tests headless (CI) or headed (local debugging)
- **Coverage**: User onboarding, todo management, assignment workflows, notifications

Run UI tests (headless - default for CI):
```bash
mvn test -pl ui-tests
```

Run UI tests in headed mode (for visual debugging):
```bash
mvn test -pl ui-tests -Dheadless=false
```

Run specific UI test:
```bash
mvn test -pl ui-tests -Dtest=UserOnboardingTest#shouldAllowNewUserToRegisterAndLoginSuccessfully
```

### Test Categories
- **Unit Tests**: Individual component testing in the backend
- **API Integration Tests**: End-to-end REST API testing
- **UI End-to-End Tests**: Browser-based workflow testing
- **Security Tests**: Authentication, authorization, and permission validation

### Allure Test Reports

The project generates **combined Allure reports** aggregating results from all test modules.

#### Generate Reports Locally

After running tests, generate the combined report:

```bash
# Install Allure CLI (if not already installed)
curl -Lo allure-2.20.1.tgz https://github.com/allure-framework/allure2/releases/download/2.20.1/allure-2.20.1.tgz
tar -zxf allure-2.20.1.tgz
export PATH="$(pwd)/allure-2.20.1/bin:$PATH"

# Generate combined report from all modules
allure generate todo-app/target/allure-results api-tests/target/allure-results ui-tests/target/allure-results --clean -o combined-allure-report

# Open report in browser
allure open combined-allure-report
```

#### CI/CD Integration

The GitHub Actions workflow (`/.github/workflows/ci.yml`) automatically:
1. Runs all tests (unit, API, UI)
2. Collects Allure results from all modules
3. Generates a combined Allure report
4. Uploads both the report and raw results as artifacts

Access reports:
- Download **allure-report** artifact from GitHub Actions
- Extract and open `index.html` in a browser

#### Module Identification

Tests are automatically labeled with their module type for easy filtering in Allure reports:
- **API Tests**: `module: API` label on all tests
- **UI Tests**: `module: UI` label on all tests

Use Allure's label filter to show only API or only UI tests.

### Test Execution

Run all tests (unit + API + UI):
```bash
mvn clean test
```

Run unit tests only:
```bash
mvn test -pl todo-app
```

Run API tests only:
```bash
mvn test -pl api-tests
```

Run UI tests only:
```bash
mvn test -pl ui-tests
```

Run specific test class:
```bash
mvn test -Dtest=TodoRepositoryTest
```

Run specific test method:
```bash
mvn test -Dtest=UserOnboardingTest#shouldAllowNewUserToRegisterAndLoginSuccessfully
```

### Test Reports

View test reports in target directories:
```bash
# Unit test reports
target/surefire-reports/

# API test results
api-tests/target/allure-results/

# UI test results
ui-tests/target/allure-results/
```

For detailed documentation on each test module, see:
- [API Tests Guide](api-tests/README.md) - RestAssured, test structure, how to run and generate reports
- [UI Tests Guide](ui-tests/README.md) - Selenium, Page Object Model, how to run and generate reports

## Troubleshooting

### Port 8091 Already in Use
```bash
# Find process using port 8091
lsof -i :8091
# Kill the process
kill -9 <PID>
# Or use a different port:
java -jar target/todo-app-0.0.1-SNAPSHOT.jar --server.port=8092
```

### Database Issues
- Clear database: Delete the `todo.db` file from `/data` directory
- Reset H2: Happens automatically on application restart

### Docker Issues
```bash
# Remove all containers and volumes
docker system prune -a -v

# Rebuild image
docker build -t todo-app --no-cache .
```

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support & Contact

For issues, questions, or suggestions:
- Create an issue in the GitHub repository
- Contact the development team at support@todoapp.com

---

**Last Updated**: January 2026 
**Project Version**: 1.0.0  
**Java Version**: 21  
**Spring Boot Version**: 3.2.5
