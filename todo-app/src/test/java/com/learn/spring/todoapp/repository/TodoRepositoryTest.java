package com.learn.spring.todoapp.repository;

import com.learn.spring.todoapp.config.TestConfig;
import com.learn.spring.todoapp.entity.Todo;
import com.learn.spring.todoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User("testuser", "password", "test@example.com");
        userRepository.save(testUser);

        // Create a test todo
        testTodo = new Todo(null, "testuser", "Test Todo", LocalDate.now().plusDays(1), false);
        testTodo.setUser(testUser);
        todoRepository.save(testTodo);
    }

    @Test
    void findByUsername_ShouldReturnTodosForUser() {
        // When
        List<Todo> todos = todoRepository.findByUsername("testuser");

        // Then
        assertFalse(todos.isEmpty());
        assertEquals(1, todos.size());
        assertEquals("Test Todo", todos.getFirst().getDescription());
        assertEquals("testuser", todos.getFirst().getUsername());
    }

    @Test
    void findById_ShouldReturnTodoWithGivenId() {
        // When
        Optional<Todo> foundTodo = todoRepository.findById(testTodo.getId());

        // Then
        assertTrue(foundTodo.isPresent());
        assertEquals("Test Todo", foundTodo.get().getDescription());
    }

    @Test
    void deleteById_ShouldRemoveTodoWithGivenId() {
        // Given
        int todoId = testTodo.getId();

        // When
        todoRepository.deleteById(todoId);
        Optional<Todo> deletedTodo = todoRepository.findById(todoId);

        // Then
        assertFalse(deletedTodo.isPresent());
    }

    @Test
    void save_ShouldCreateNewTodo() {
        // Given
        Todo newTodo = new Todo(null, "testuser", "Another Todo", LocalDate.now().plusDays(2), false);
        newTodo.setUser(testUser);

        // When
        Todo savedTodo = todoRepository.save(newTodo);

        // Then
        assertNotNull(savedTodo.getId());
        assertEquals("Another Todo", savedTodo.getDescription());
        
        // Verify it's in the database
        Optional<Todo> foundTodo = todoRepository.findById(savedTodo.getId());
        assertTrue(foundTodo.isPresent());
    }

    @Test
    void save_ShouldUpdateExistingTodo() {
        // Given
        testTodo.setDescription("Updated Description");
        testTodo.setDone(true);

        // When
        Todo updatedTodo = todoRepository.save(testTodo);

        // Then
        assertEquals("Updated Description", updatedTodo.getDescription());
        assertTrue(updatedTodo.isDone());
        
        // Verify it's updated in the database
        Optional<Todo> foundTodo = todoRepository.findById(testTodo.getId());
        assertTrue(foundTodo.isPresent());
        assertEquals("Updated Description", foundTodo.get().getDescription());
        assertTrue(foundTodo.get().isDone());
    }
}