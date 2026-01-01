package com.example.todo.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {
    private Integer id;
    private String description;
    private LocalDate targetDate;
    private boolean done;
}