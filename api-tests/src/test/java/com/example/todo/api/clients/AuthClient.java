package com.example.todo.api.clients;

import io.restassured.response.Response;
import com.example.todo.api.config.ConfigurationManager;

import static io.restassured.RestAssured.given;

public class AuthClient {

    public String getAuthToken(String username, String password) {
        Response response = given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", username)
                .formParam("password", password)
                .redirects().follow(false) // Don't follow redirect to welcome page
                .post("/login");

        // Check for login failure (Spring Security redirects to /login?error)
        String location = response.getHeader("Location");
        if (location != null && location.contains("error")) {
            return null;
        }

        // Spring Security Form Login sets JSESSIONID
        return response.getCookie("JSESSIONID");
    }
}