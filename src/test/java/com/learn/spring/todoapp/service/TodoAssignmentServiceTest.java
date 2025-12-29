package com.learn.spring.todoapp.service;

import com.learn.spring.todoapp.entity.*;
import com.learn.spring.todoapp.repository.TodoAssignmentRepository;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoAssignmentServiceTest {

    @Mock
    private TodoAssignmentRepository assignmentRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TodoAssignmentService todoAssignmentService;

    private User assigner;
    private User assignee;
    private Todo todo;
    private TodoAssignment assignment;

    @BeforeEach
    void setUp() {
        assigner = new User("assigner", "password", "assigner@example.com");
        assignee = new User("assignee", "password", "assignee@example.com");
        todo = new Todo(1, "assigner", "Test Todo", LocalDate.now().plusDays(1), false);
        todo.setUser(assigner);

        assignment = new TodoAssignment();
        assignment.setId(1L);
        assignment.setTodo(todo);
        assignment.setAssigner(assigner);
        assignment.setAssignee(assignee);
        assignment.setStatus(AssignmentStatus.PENDING);
    }

    @Test
    void assignTodo_ShouldCreateAssignment_WhenValid() {
        // Given
        when(todoRepository.findById(1)).thenReturn(Optional.of(todo));
        when(userRepository.findByUsername("assigner")).thenReturn(Optional.of(assigner));
        when(userRepository.findByUsername("assignee")).thenReturn(Optional.of(assignee));
        when(assignmentRepository.findByTodoId(1)).thenReturn(Collections.emptyList());
        when(assignmentRepository.save(any(TodoAssignment.class))).thenReturn(assignment);

        // When
        TodoAssignment result = todoAssignmentService.assignTodo(1, "assigner", "assignee");

        // Then
        assertNotNull(result);
        assertEquals(AssignmentStatus.PENDING, result.getStatus());
        verify(notificationService, times(1)).createNotification(eq(assignee), anyString(), eq(1L));
    }

    @Test
    void assignTodo_ShouldThrowException_WhenAssignerIsNotOwner() {
        // Given
        when(todoRepository.findById(1)).thenReturn(Optional.of(todo));

        // When/Then
        assertThrows(IllegalStateException.class, () -> 
            todoAssignmentService.assignTodo(1, "otherUser", "assignee")
        );
    }

    @Test
    void assignTodo_ShouldThrowException_WhenTodoAlreadyAssigned() {
        // Given
        when(todoRepository.findById(1)).thenReturn(Optional.of(todo));
        when(userRepository.findByUsername("assigner")).thenReturn(Optional.of(assigner));
        when(userRepository.findByUsername("assignee")).thenReturn(Optional.of(assignee));
        when(assignmentRepository.findByTodoId(1)).thenReturn(Collections.singletonList(assignment));

        // When/Then
        assertThrows(IllegalStateException.class, () -> 
            todoAssignmentService.assignTodo(1, "assigner", "assignee")
        );
    }

    @Test
    void respondToAssignment_ShouldAcceptAssignment() {
        // Given
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(TodoAssignment.class))).thenReturn(assignment);

        // When
        TodoAssignment result = todoAssignmentService.respondToAssignment(1L, "assignee", true, LocalDate.now().plusDays(2), null);

        // Then
        assertEquals(AssignmentStatus.ACCEPTED, result.getStatus());
        assertEquals(LocalDate.now().plusDays(2), result.getTentativeCompletionDate());
        verify(notificationService, times(1)).createNotification(eq(assigner), anyString(), eq(1L));
    }

    @Test
    void respondToAssignment_ShouldDeclineAssignment() {
        // Given
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(TodoAssignment.class))).thenReturn(assignment);

        // When
        TodoAssignment result = todoAssignmentService.respondToAssignment(1L, "assignee", false, null, "Too busy");

        // Then
        assertEquals(AssignmentStatus.DECLINED, result.getStatus());
        assertEquals("Too busy", result.getDeclineReason());
        verify(notificationService, times(1)).createNotification(eq(assigner), anyString(), eq(1L));
    }

    @Test
    void respondToAssignment_ShouldThrowException_WhenUserIsNotAssignee() {
        // Given
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        // When/Then
        assertThrows(IllegalStateException.class, () -> 
            todoAssignmentService.respondToAssignment(1L, "otherUser", true, null, null)
        );
    }

    @Test
    void completeAssignment_ShouldUpdateStatusToCompleted() {
        // Given
        assignment.setStatus(AssignmentStatus.ACCEPTED);
        when(assignmentRepository.findByTodoId(1)).thenReturn(Collections.singletonList(assignment));

        // When
        todoAssignmentService.completeAssignment(1);

        // Then
        assertEquals(AssignmentStatus.COMPLETED, assignment.getStatus());
        verify(assignmentRepository, times(1)).save(assignment);
        verify(notificationService, times(1)).createNotification(eq(assigner), anyString(), eq(1L));
    }
}
