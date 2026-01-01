package com.learn.spring.todoapp.service;

import com.learn.spring.todoapp.entity.*;
import com.learn.spring.todoapp.repository.TodoAssignmentRepository;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoAssignmentService {

    private final TodoAssignmentRepository assignmentRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public TodoAssignmentService(TodoAssignmentRepository assignmentRepository,
                                 TodoRepository todoRepository,
                                 UserRepository userRepository,
                                 NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public TodoAssignment assignTodo(Integer todoId, String assignerUsername, String assigneeUsername) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        if (!todo.getUsername().equals(assignerUsername)) {
            throw new IllegalStateException("Only the owner can assign this todo");
        }

        User assigner = userRepository.findByUsername(assignerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Assigner not found"));
        User assignee = userRepository.findByUsername(assigneeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));

        // Check if already assigned and pending/accepted
        List<TodoAssignment> existingAssignments = assignmentRepository.findByTodoId(todoId);
        boolean hasActiveAssignment = existingAssignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ACCEPTED);

        if (hasActiveAssignment) {
            throw new IllegalStateException("Todo is already assigned");
        }

        TodoAssignment assignment = new TodoAssignment();
        assignment.setTodo(todo);
        assignment.setAssigner(assigner);
        assignment.setAssignee(assignee);
        assignment.setStatus(AssignmentStatus.PENDING);
        
        TodoAssignment savedAssignment = assignmentRepository.save(assignment);

        notificationService.createNotification(assignee, 
            "User " + assignerUsername + " has assigned you a new todo: '" + todo.getDescription() + "'", 
            Long.valueOf(todoId));

        return savedAssignment;
    }

    public TodoAssignment respondToAssignment(Long assignmentId, String assigneeUsername, boolean accepted, LocalDate tentativeDate, String declineReason) {
        TodoAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getAssignee().getUsername().equals(assigneeUsername)) {
            throw new IllegalStateException("Not authorized to respond to this assignment");
        }

        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new IllegalStateException("Assignment is not in PENDING state");
        }

        assignment.setRespondedAt(LocalDateTime.now());
        if (accepted) {
            assignment.setStatus(AssignmentStatus.ACCEPTED);
            assignment.setTentativeCompletionDate(tentativeDate);
            notificationService.createNotification(assignment.getAssigner(),
                    "User " + assigneeUsername + " accepted your assignment for: '" + assignment.getTodo().getDescription() + "'",
                    Long.valueOf(assignment.getTodo().getId()));
        } else {
            assignment.setStatus(AssignmentStatus.DECLINED);
            assignment.setDeclineReason(declineReason);
            notificationService.createNotification(assignment.getAssigner(),
                    "User " + assigneeUsername + " declined your assignment for: '" + assignment.getTodo().getDescription() + "'",
                    Long.valueOf(assignment.getTodo().getId()));
        }

        return assignmentRepository.save(assignment);
    }
    
    public void completeAssignment(Integer todoId) {
        List<TodoAssignment> assignments = assignmentRepository.findByTodoId(todoId);
        Optional<TodoAssignment> activeAssignment = assignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.ACCEPTED)
                .findFirst();

        if (activeAssignment.isPresent()) {
            TodoAssignment assignment = activeAssignment.get();
            assignment.setStatus(AssignmentStatus.COMPLETED);
            assignmentRepository.save(assignment);
            
            notificationService.createNotification(assignment.getAssigner(),
                    "User " + assignment.getAssignee().getUsername() + " completed the task: '" + assignment.getTodo().getDescription() + "'",
                    Long.valueOf(todoId));
        }
    }
    
    public List<TodoAssignment> getAssignmentsForUser(String username) {
        return assignmentRepository.findByAssigneeUsername(username);
    }
}
