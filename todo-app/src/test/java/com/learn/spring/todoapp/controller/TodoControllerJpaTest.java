package com.learn.spring.todoapp.controller;

import com.learn.spring.todoapp.config.TodoControllerJpaTestConfig;
import com.learn.spring.todoapp.entity.Todo;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import com.learn.spring.todoapp.service.TodoAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoControllerJpa.class)
@Import(TodoControllerJpaTestConfig.class)
@ActiveProfiles("test")
public class TodoControllerJpaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository todoRepository;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private TodoAssignmentService todoAssignmentService;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", "test@example.com");
        testTodo = new Todo(1, "testuser", "Test Todo", LocalDate.now().plusDays(1), false);
        testTodo.setUser(testUser);
    }

    @Test
    @WithMockUser(username = "testuser")
    void listAllTodos_ShouldReturnTodosForCurrentUser() throws Exception {
        // Given
        List<Todo> todos = Collections.singletonList(testTodo);
        when(todoRepository.findByUsername("testuser")).thenReturn(todos);
        when(todoAssignmentService.getAssignmentsForUser("testuser")).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().isOk())
                .andExpect(view().name("listTodos"))
                .andExpect(model().attribute("todos", todos))
                .andExpect(model().attributeExists("assignedTodos"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-testid=\"todo-assign-button\"")));

        verify(todoRepository, times(1)).findByUsername("testuser");
        verify(todoAssignmentService, times(1)).getAssignmentsForUser("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void showNewTodoPage_ShouldDisplayTodoForm() throws Exception {
        // When/Then
        mockMvc.perform(get("/add-todo"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addNewTodo_ShouldCreateTodoAndRedirect() throws Exception {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When/Then
        mockMvc.perform(post("/add-todo")
                .param("description", "New Todo")
                .param("targetDate", LocalDate.now().plusDays(1).toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list-todos"));

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTodo_ShouldRemoveTodoAndRedirect() throws Exception {
        // Given
        when(todoRepository.findById(1)).thenReturn(Optional.of(testTodo));
        doNothing().when(todoRepository).deleteById(1);

        // When/Then
        mockMvc.perform(delete("/todos/{id}", 1).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/list-todos"));

        verify(todoRepository, times(1)).findById(1);
        verify(todoRepository, times(1)).deleteById(1);
    }

    @Test
    @WithMockUser(username = "testuser")
    void showUpdateTodoPage_ShouldDisplayTodoFormWithExistingTodo() throws Exception {
        // Given
        when(todoRepository.findById(1)).thenReturn(Optional.of(testTodo));

        // When/Then
        mockMvc.perform(get("/todos/{id}", 1))
            .andExpect(status().isOk())
            .andExpect(view().name("todo"))
            .andExpect(model().attribute("todo", testTodo));

        verify(todoRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateTodo_ShouldUpdateTodoAndRedirect() throws Exception {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(todoRepository.findById(1)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When/Then
        mockMvc.perform(put("/todos/{id}", 1)
            .param("id", "1")
            .param("description", "Updated Todo")
            .param("targetDate", LocalDate.now().plusDays(1).toString())
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/list-todos"));

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(todoRepository, times(1)).findById(1);
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoAssignmentService, never()).completeAssignment(anyInt());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void updateTodo_ShouldCompleteAssignmentWhenTodoIsDone() throws Exception {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(todoRepository.findById(1)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When/Then
        mockMvc.perform(put("/todos/{id}", 1)
            .param("id", "1")
            .param("description", "Updated Todo")
            .param("targetDate", LocalDate.now().plusDays(1).toString())
            .param("done", "true")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/list-todos"));

        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoAssignmentService, times(1)).completeAssignment(1);
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTodo_ShouldThrowExceptionWhenTodoDoesNotBelongToUser() {
        // Given
        Todo otherUserTodo = new Todo(1, "otheruser", "Other User's Todo", LocalDate.now().plusDays(1), false);
        User otherUser = new User("otheruser", "password", "other@example.com");
        otherUserTodo.setUser(otherUser);

        when(todoRepository.findById(1)).thenReturn(Optional.of(otherUserTodo));

        // When/Then
        try {
                mockMvc.perform(delete("/todos/{id}", 1).with(csrf()));
            // If we get here, the test should fail
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Verify that the exception is an IllegalStateException with the correct message
            assertInstanceOf(IllegalStateException.class, e.getCause());
            assertEquals("Not authorized to delete this todo", e.getCause().getMessage());
        }

        verify(todoRepository, times(1)).findById(1);
        verify(todoRepository, times(0)).deleteById(anyInt());
    }
}
