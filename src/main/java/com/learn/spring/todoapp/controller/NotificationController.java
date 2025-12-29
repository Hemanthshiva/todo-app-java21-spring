package com.learn.spring.todoapp.controller;

import com.learn.spring.todoapp.dto.NotificationDTO;
import com.learn.spring.todoapp.entity.Notification;
import com.learn.spring.todoapp.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Manage user notifications for task assignments and updates")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(
        summary = "Get unread notifications",
        description = "Retrieves all unread notifications for the currently authenticated user. " +
                      "Notifications include updates about task assignments, acceptances, declines, and completions."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved notifications",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = NotificationDTO.class))
            )
        ),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public List<NotificationDTO> getUnreadNotifications() {
        String username = getLoggedInUsername();
        return notificationService.getUnreadUserNotifications(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @PostMapping("/{id}/read")
    @Operation(
        summary = "Mark notification as read",
        description = "Marks a specific notification as read, removing it from the unread notifications list."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification successfully marked as read"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<?> markAsRead(
            @Parameter(description = "The ID of the notification to mark as read", required = true)
            @PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().body("{\"message\": \"Notification marked as read\"}");
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setRelatedTodoId(notification.getRelatedTodoId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    private String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Unauthenticated access");
        }
        return authentication.getName();
    }
}
