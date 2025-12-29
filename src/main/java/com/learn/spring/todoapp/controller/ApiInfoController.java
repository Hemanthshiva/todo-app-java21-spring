package com.learn.spring.todoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Application Navigation", description = "Home page and navigation endpoints")
public class ApiInfoController {

    @GetMapping("/api-info")
    @Operation(
        summary = "API information page",
        description = "Displays a user-friendly overview of the API endpoints and how to use them. Provides links to Swagger UI and authentication instructions."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API info page successfully displayed"),
        @ApiResponse(responseCode = "404", description = "Page not found")
    })
    public String showApiInfo() {
        return "api-info";
    }
}