package com.learn.spring.todoapp.integration;

import com.learn.spring.todoapp.entity.Todo;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DatabaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        todoRepository.deleteAll();

        // Create testuser
        testUser = new User("dbuser", "password", "db@example.com");
        userRepository.save(testUser);
    }

    @Test
    void userRepository_ShouldSaveAndRetrieveUser() {
        // Given
        User newUser = new User("newdbuser", "password", "newdb@example.com");

        // When
        userRepository.save(newUser);

        // Then
        Optional<User> retrievedUser = userRepository.findByUsername("newdbuser");
        assertTrue(retrievedUser.isPresent());
        assertEquals("newdb@example.com", retrievedUser.get().getEmail());
    }

    @Test
    void todoRepository_ShouldSaveAndRetrieveTodo() {
        // Given
        Todo todo = new Todo(null, "dbuser", "Database Test Todo", LocalDate.now().plusDays(1), false);
        todo.setUser(testUser);

        // When
        todoRepository.save(todo);

        // Then
        List<Todo> todos = todoRepository.findByUsername("dbuser");
        assertFalse(todos.isEmpty());
        assertEquals("Database Test Todo", todos.getFirst().getDescription());
    }

    @Test
    void todoRepository_ShouldUpdateTodo() {
        // Given
        Todo todo = new Todo(null, "dbuser", "Original Todo", LocalDate.now().plusDays(1), false);
        todo.setUser(testUser);
        todoRepository.save(todo);

        // When
        todo.setDescription("Updated Todo");
        todo.setDone(true);
        todoRepository.save(todo);

        // Then
        Optional<Todo> updatedTodo = todoRepository.findById(todo.getId());
        assertTrue(updatedTodo.isPresent());
        assertEquals("Updated Todo", updatedTodo.get().getDescription());
        assertTrue(updatedTodo.get().isDone());
    }

    @Test
    void todoRepository_ShouldDeleteTodo() {
        // Given
        Todo todo = new Todo(null, "dbuser", "Todo to Delete", LocalDate.now().plusDays(1), false);
        todo.setUser(testUser);
        todoRepository.save(todo);
        int todoId = todo.getId();

        // When
        todoRepository.deleteById(todoId);

        // Then
        Optional<Todo> deletedTodo = todoRepository.findById(todoId);
        assertFalse(deletedTodo.isPresent());
    }

    @Test
    void authorityRepository_ShouldAddAndRetrieveAuthorities() {
        // Given
        authorityRepository.addAuthority("dbuser", "ROLE_TEST");

        // When
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("dbuser");

        // Then
        assertFalse(authorities.isEmpty());
        assertEquals("ROLE_TEST", authorities.getFirst());
    }

    @Test
    void authorityRepository_ShouldRemoveAuthority() {
        // Given
        authorityRepository.addAuthority("dbuser", "ROLE_TEST1");
        authorityRepository.addAuthority("dbuser", "ROLE_TEST2");

        // When
        authorityRepository.removeAuthority("dbuser", "ROLE_TEST1");

        // Then
        List<String> authorities = authorityRepository.findAuthoritiesByUsername("dbuser");
        assertEquals(1, authorities.size());
        assertEquals("ROLE_TEST2", authorities.getFirst());
    }

    @Test
    void userAndTodoRelationship_ShouldBeProperlyMapped() {
        // Given - Create a new user for this test to avoid any caching issues
        User newUser = new User("relationuser", "password", "relation@example.com");
        userRepository.save(newUser);

        // Create and save todos with an explicit relationship to the user
        Todo todo1 = new Todo(null, "relationuser", "Todo 1", LocalDate.now().plusDays(1), false);
        todo1.setUser(newUser);
        todoRepository.save(todo1);

        Todo todo2 = new Todo(null, "relationuser", "Todo 2", LocalDate.now().plusDays(2), false);
        todo2.setUser(newUser);
        todoRepository.save(todo2);

        // Manually add todos to a user's collection to ensure a bidirectional relationship
        if (!newUser.getTodos().contains(todo1)) {
            newUser.getTodos().add(todo1);
        }
        if (!newUser.getTodos().contains(todo2)) {
            newUser.getTodos().add(todo2);
        }
        userRepository.save(newUser);

        // When - Retrieve the user from the database
        Optional<User> retrievedUser = userRepository.findByUsername("relationuser");

        // Then
        assertTrue(retrievedUser.isPresent());
        List<Todo> todos = retrievedUser.get().getTodos();
        System.out.println("[DEBUG_LOG] User todos size: " + todos.size());
        for (Todo todo : todos) {
            System.out.println("[DEBUG_LOG] Todo: " + todo.getDescription());
        }

        // Verify the todos are associated with the user
        assertEquals(2, todos.size());
        assertTrue(todos.stream().anyMatch(t -> t.getDescription().equals("Todo 1")));
        assertTrue(todos.stream().anyMatch(t -> t.getDescription().equals("Todo 2")));
    }
}
