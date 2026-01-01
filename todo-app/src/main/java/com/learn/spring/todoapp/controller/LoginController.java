package com.learn.spring.todoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Application Navigation", description = "Home page and navigation endpoints")
public class LoginController {

    @GetMapping("/")
    @Operation(
        summary = "Home page - Root redirect",
        description = "The root endpoint that redirects to the login page. This is the entry point of the application."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to /login page"),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user will be redirected to login")
    })
    public String root() {
        return "redirect:/login";
    }
}
