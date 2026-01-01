package com.example.todo.api.clients;

import com.example.todo.api.config.ConfigurationManager;
import com.example.todo.api.models.AssignTodoRequestDto;
import com.example.todo.api.models.RespondAssignmentRequestDto;
import io.restassured.response.Response;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class AssignmentClient {

    public Response assignTodo(int todoId, String assigneeUsername, String token) {
        AssignTodoRequestDto request = AssignTodoRequestDto.builder()
                .assigneeUsername(assigneeUsername)
                .build();

        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .contentType(ContentType.JSON)
                .body(request)
                .post("/todos/" + todoId + "/assign");
    }

    public Response respondToAssignment(long assignmentId, RespondAssignmentRequestDto request, String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .contentType(ContentType.JSON)
                .body(request)
                .post("/assignments/" + assignmentId + "/respond");
    }
}