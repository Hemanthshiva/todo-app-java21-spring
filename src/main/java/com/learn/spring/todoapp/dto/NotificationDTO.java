package com.learn.spring.todoapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "User notification containing information about task assignments and updates")
public class NotificationDTO {
    @Schema(description = "Unique identifier of the notification", example = "123")
    private Long id;

    @Schema(description = "Notification message content", example = "User john_doe assigned a task to you")
    private String message;

    @Schema(description = "Whether the notification has been read", example = "false")
    private boolean isRead;

    @Schema(description = "The ID of the related todo task (if applicable)", example = "5")
    private Long relatedTodoId;

    @Schema(description = "Timestamp when the notification was created", example = "2024-12-29T10:30:00")
    private LocalDateTime createdAt;
}
