package com.learn.spring.todoapp.repository;

import com.learn.spring.todoapp.entity.TodoAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TodoAssignmentRepository extends JpaRepository<TodoAssignment, Long> {
    List<TodoAssignment> findByAssigneeUsername(String assigneeUsername);
    List<TodoAssignment> findByAssignerUsername(String assignerUsername);
    List<TodoAssignment> findByTodoId(Integer todoId);
}
