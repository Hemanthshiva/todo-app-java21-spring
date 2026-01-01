package com.learn.spring.todoapp.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.learn.spring.todoapp.entity.Todo;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import com.learn.spring.todoapp.service.TodoAssignmentService;

import java.time.LocalDate;
import java.util.List;

@Controller
@SessionAttributes("name")
@Tag(name = "Todo Management", description = "Create, read, update, and delete todo tasks")
public class TodoControllerJpa {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoAssignmentService todoAssignmentService;

    public TodoControllerJpa(TodoRepository todoRepository, UserRepository userRepository, TodoAssignmentService todoAssignmentService) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.todoAssignmentService = todoAssignmentService;
    }

    @GetMapping("list-todos")
    @Operation(
        summary = "List all user todos",
        description = "Retrieves and displays all todos created by the currently authenticated user, " +
                      "as well as todos that have been assigned to the user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo list page successfully displayed"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public String listAllTodos(ModelMap model) {
        String username = getLoggedInUsername();
        List<Todo> todos = todoRepository.findByUsername(username);
        model.addAttribute("todos", todos);
        
        var assignments = todoAssignmentService.getAssignmentsForUser(username);
        model.addAttribute("assignedTodos", assignments);
        
        return "listTodos";
    }

    @GetMapping("add-todo")
    @Operation(
        summary = "Show create todo form",
        description = "Displays the form page for creating a new todo task. Pre-fills with default values."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Create todo form page successfully displayed"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public String showNewTodoPage(ModelMap model) {
        String username = getLoggedInUsername();
        Todo todo = new Todo(0, username, "", LocalDate.now().plusMonths(1), false);
        model.put("todo", todo);
        return "todo";
    }

    @PostMapping("add-todo")
    @Operation(
        summary = "Create a new todo",
        description = "Processes the form submission to create a new todo task with description, target date, and status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Todo created successfully, redirects to list-todos"),
        @ApiResponse(responseCode = "400", description = "Validation error in form submission"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public String addNewTodo(@Valid Todo todo, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "todo";
        }

        String username = getLoggedInUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        todo.setUser(user); // This will also set the username field
        todoRepository.save(todo);

        return "redirect:/list-todos";
    }

    @DeleteMapping("/todos/{id}")
    @Operation(
        summary = "Delete a todo",
        description = "Deletes a specific todo by its ID. Only the owner of the todo can delete it."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Todo deleted successfully, redirects to list-todos"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete this todo"),
        @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public String deleteTodo(
            @Parameter(description = "The ID of the todo to delete", required = true)
            @PathVariable Integer id) {
        // Verify the todo belongs to the current user before deleting
        String username = getLoggedInUsername();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));

        if (!todo.getUsername().equals(username)) {
            throw new IllegalStateException("Not authorized to delete this todo");
        }

        todoRepository.deleteById(id);
        return "redirect:/list-todos";
    }

    @GetMapping("/todos/{id}")
    @Operation(
        summary = "Show update todo form",
        description = "Displays the form page for editing an existing todo. The todo must be owned by the current user or assigned to them."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Update todo form page successfully displayed"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this todo"),
        @ApiResponse(responseCode = "404", description = "Todo not found")
    })
        public String showUpdateTodoPage(
            @Parameter(description = "The ID of the todo to update", required = true)
            @PathVariable Integer id, ModelMap model) {
        // Verify the todo belongs to the current user
        String username = getLoggedInUsername();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));

        boolean isOwner = todo.getUsername().equals(username);
        boolean isAssignee = todo.getAssignments().stream()
                .anyMatch(a -> a.getAssignee().getUsername().equals(username) &&
                        (a.getStatus() == com.learn.spring.todoapp.entity.AssignmentStatus.ACCEPTED));

        if (!isOwner && !isAssignee) {
            throw new IllegalStateException("Not authorized to update this todo");
        }

        model.addAttribute("todo", todo);
        return "todo";
    }

    @PutMapping("/todos/{id}")
    @Operation(
        summary = "Update an existing todo",
        description = "Processes the form submission to update a todo's description, target date, and completion status. " +
                      "Marks assignments as complete if the todo is marked as done."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Todo updated successfully, redirects to list-todos"),
        @ApiResponse(responseCode = "400", description = "Validation error in form submission"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this todo"),
        @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    public String updateTodo(@PathVariable Integer id, @Valid Todo todo, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "todo";
        }

        String username = getLoggedInUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        // Verify the todo belongs to the current user OR is assigned to the current user
        Todo existingTodo = todoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid todo Id:" + id));

        boolean isOwner = existingTodo.getUsername().equals(username);
        // Check if assigned to user
        boolean isAssignee = existingTodo.getAssignments().stream()
                .anyMatch(a -> a.getAssignee().getUsername().equals(username) && 
                              (a.getStatus() == com.learn.spring.todoapp.entity.AssignmentStatus.ACCEPTED));

        if (!isOwner && !isAssignee) {
             throw new IllegalStateException("Not authorized to update this todo");
        }
        
           if (isAssignee) {
               todo.setUser(existingTodo.getUser()); // Keep original owner
           } else {
               todo.setUser(user);
           }

           // ensure the id is set on the incoming todo object
           todo.setId(id);

           todoRepository.save(todo);

           if (todo.isDone()) {
              todoAssignmentService.completeAssignment(id);
           }

        return "redirect:/list-todos";
    }

    private String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Unauthenticated access");
        }
        return authentication.getName();
    }
}
