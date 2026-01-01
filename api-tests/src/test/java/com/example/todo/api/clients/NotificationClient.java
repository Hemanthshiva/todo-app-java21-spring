package com.example.todo.api.clients;

import com.example.todo.api.config.ConfigurationManager;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class NotificationClient {

    public Response getUnreadNotifications(String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .get("/api/notifications");
    }

    public Response markAsRead(Long id, String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .post("/api/notifications/" + id + "/read");
    }
}