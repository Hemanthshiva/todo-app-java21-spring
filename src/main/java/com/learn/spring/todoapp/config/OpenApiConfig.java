package com.learn.spring.todoapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo Application - Collaborative Task Management API")
                        .version("1.0.0")
                        .description("A modern collaborative task management system built with Spring Boot 3.2.5 and Java 21. " +
                                "This application provides comprehensive todo management, user authentication, task assignment workflows, " +
                                "and real-time notifications. Features include create/read/update/delete operations for todos, collaborative " +
                                "task assignment with accept/decline workflow, real-time notifications for task events, user search and " +
                                "registration, and secure authentication with Spring Security. The API supports both traditional form-based " +
                                "interactions and REST endpoints for programmatic access. Perfect for teams and individuals looking for a " +
                                "flexible, scalable solution for task management and collaboration.")
                        .contact(new Contact()
                                .name("Todo App Development Team")
                                .email("support@todoapp.com")
                                .url("https://github.com/Hemanthshiva/todo-app-java21-spring"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic Authentication using username and password")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}