package com.learn.spring.todoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Application Navigation", description = "Home page and navigation endpoints")
public class WelcomeController {

    @GetMapping("/welcome")
    @Operation(
        summary = "Welcome page",
        description = "Displays the welcome/dashboard page for authenticated users. Shows an overview of their todos and assigned tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Welcome page successfully displayed"),
        @ApiResponse(responseCode = "401", description = "Authentication required - redirects to login")
    })
    public String welcome() {
        return "welcome";
    }
}