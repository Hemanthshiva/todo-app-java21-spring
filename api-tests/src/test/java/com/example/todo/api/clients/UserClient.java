package com.example.todo.api.clients;

import com.example.todo.api.config.ConfigurationManager;
import com.example.todo.api.models.UserDto;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {

    public Response registerUser(UserDto userDto) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", userDto.getUsername())
                .formParam("password", userDto.getPassword())
                .formParam("confirmPassword", userDto.getConfirmPassword())
                .formParam("email", userDto.getEmail())
                .redirects().follow(false)
                .post("/register");
    }
}