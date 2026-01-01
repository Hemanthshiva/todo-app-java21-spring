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
public class RespondAssignmentRequestDto {
    private String action; // "accept" or "decline"
    private LocalDate tentativeCompletionDate;
    private String declineReason;
}