package com.learn.spring.todoapp.controller;

import com.learn.spring.todoapp.dto.AssignTodoRequest;
import com.learn.spring.todoapp.dto.RespondAssignmentRequest;
import com.learn.spring.todoapp.service.TodoAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Task Assignments", description = "Manage task assignments and collaborative workflows")
public class TodoAssignmentController {

    private final TodoAssignmentService assignmentService;

    public TodoAssignmentController(TodoAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/todos/{todoId}/assign")
    @Operation(
        summary = "Assign a todo to another user",
        description = "Creates a new task assignment, allowing the current user to assign their todo to another user. " +
                      "The assigned user will receive a notification and can accept or decline the assignment."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo successfully assigned"),
        @ApiResponse(responseCode = "400", description = "Invalid request or user not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public ResponseEntity<?> assignTodo(
            @Parameter(description = "The ID of the todo to assign", required = true)
            @PathVariable Integer todoId, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Assignment request containing the assignee username",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AssignTodoRequest.class)
                )
            )
            @RequestBody AssignTodoRequest request) {
        String assignerUsername = getLoggedInUsername();
        assignmentService.assignTodo(todoId, assignerUsername, request.getAssigneeUsername());
        return ResponseEntity.ok().body("{\"message\": \"Todo assigned successfully\"}");
    }

    @PostMapping("/assignments/{assignmentId}/respond")
    @Operation(
        summary = "Respond to a task assignment",
        description = "Accept or decline a task assignment. If accepted, set a tentative completion date. " +
                      "If declined, provide an optional reason. The original assigner will be notified of the response."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assignment response processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or missing required fields"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    public ResponseEntity<?> respondToAssignment(
            @Parameter(description = "The ID of the assignment to respond to", required = true)
            @PathVariable Long assignmentId, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Response containing action (accept/decline) and optional completion date or decline reason",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RespondAssignmentRequest.class)
                )
            )
            @RequestBody RespondAssignmentRequest request) {
        String assigneeUsername = getLoggedInUsername();
        boolean accepted = "accept".equalsIgnoreCase(request.getAction());
        assignmentService.respondToAssignment(assignmentId, assigneeUsername, accepted, request.getTentativeCompletionDate(), request.getDeclineReason());
        return ResponseEntity.ok().body("{\"message\": \"Assignment response recorded\"}");
    }

    private String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Unauthenticated access");
        }
        return authentication.getName();
    }
}
