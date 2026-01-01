package com.learn.spring.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User information for search results and task assignments")
public class UserDTO {
    @Schema(description = "The unique username identifier", example = "john_doe")
    private String username;

    @Schema(description = "The user's email address", example = "john@example.com")
    private String email;
}
