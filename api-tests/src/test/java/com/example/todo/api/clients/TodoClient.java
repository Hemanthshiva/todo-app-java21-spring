package com.example.todo.api.clients;

import com.example.todo.api.config.ConfigurationManager;
import com.example.todo.api.models.TodoDto;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TodoClient {

    public Response createTodo(TodoDto todoDto, String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .contentType("application/x-www-form-urlencoded")
                .formParam("description", todoDto.getDescription())
                .formParam("targetDate", todoDto.getTargetDate().toString()) // yyyy-MM-dd
                .formParam("done", todoDto.isDone())
                .redirects().follow(false)
                .post("/add-todo");
    }

    public Response getTodos(String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .redirects().follow(false) // Disable redirects to handle 302/401 assertions
                .get("/list-todos");
    }

    public Response updateTodo(int id, TodoDto todoDto, String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .contentType("application/x-www-form-urlencoded")
                .formParam("description", todoDto.getDescription())
                .formParam("targetDate", todoDto.getTargetDate().toString())
                .formParam("done", todoDto.isDone())
                .redirects().follow(false)
                .put("/todos/" + id);
    }

    public Response deleteTodo(int id, String token) {
        return given()
                .baseUri(ConfigurationManager.getBaseUri())
                .port(ConfigurationManager.getPort())
                .cookie("JSESSIONID", token)
                .redirects().follow(false)
                .delete("/todos/" + id);
    }
}