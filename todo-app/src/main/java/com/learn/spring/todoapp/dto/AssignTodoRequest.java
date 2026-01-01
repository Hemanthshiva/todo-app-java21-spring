package com.learn.spring.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for assigning a todo to another user")
public class AssignTodoRequest {
    @Schema(
        description = "The username of the user to assign the todo to",
        example = "john_doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String assigneeUsername;
}
