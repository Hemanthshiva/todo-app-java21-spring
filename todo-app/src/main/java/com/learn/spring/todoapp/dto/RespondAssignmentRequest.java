package com.learn.spring.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Request payload for responding to a task assignment (accept or decline)")
public class RespondAssignmentRequest {
    @Schema(
        description = "The action to take on the assignment",
        example = "accept",
        allowableValues = {"accept", "decline"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String action; // "accept" or "decline"

    @Schema(
        description = "The tentative completion date for the task (required if action is 'accept')",
        example = "2024-12-31"
    )
    private LocalDate tentativeCompletionDate;

    @Schema(
        description = "The reason for declining the assignment (optional, used when action is 'decline')",
        example = "Too busy with current tasks"
    )
    private String declineReason;
}
