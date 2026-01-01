package com.learn.spring.todoapp.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", insertable = false, updatable = false)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    private String description;

    @Column(name = "target_date")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate targetDate;

    private boolean done;

    @jakarta.persistence.OneToMany(mappedBy = "todo", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<TodoAssignment> assignments = new java.util.ArrayList<>();

    public Todo() {
    }

    public Todo(Integer id, String username, String description, LocalDate targetDate, boolean done) {
        super();
        this.id = id;
        this.username = username;
        this.description = description;
        this.targetDate = targetDate;
        this.done = done;
    }

    // Helper method to set both user and username and maintain bidirectional relationship
    public void setUser(User user) {
        // Remove from old user's todos list if exists
        if (this.user != null && this.user.getTodos() != null) {
            this.user.getTodos().remove(this);
        }

        // Set new user
        this.user = user;
        this.username = user != null ? user.getUsername() : null;

        // Add to new user's todos list if exists
        if (user != null && user.getTodos() != null) {
            user.getTodos().add(this);
        }
    }

    public TodoAssignment getActiveAssignment() {
        if (assignments == null) return null;
        return assignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ACCEPTED)
                .findFirst()
                .orElse(null);
    }
}
