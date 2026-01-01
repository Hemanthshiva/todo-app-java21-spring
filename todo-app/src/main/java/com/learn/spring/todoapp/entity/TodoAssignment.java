package com.learn.spring.todoapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TodoAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigner_username", referencedColumnName = "username", nullable = false)
    private User assigner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_username", referencedColumnName = "username", nullable = false)
    private User assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(name = "tentative_completion_date")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate tentativeCompletionDate;

    private String declineReason;

    @Column(name = "assigned_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime assignedAt;

    @Column(name = "responded_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
