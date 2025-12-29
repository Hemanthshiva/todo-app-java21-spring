package com.learn.spring.todoapp.integration;

import com.learn.spring.todoapp.entity.Todo;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TodoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        todoRepository.deleteAll();

        // Create testuser
        User testUser = new User("integrationuser", passwordEncoder.encode("password"), "integration@example.com");
        userRepository.save(testUser);
        authorityRepository.addAuthority("integrationuser", "ROLE_USER");

        // Create test todo
        testTodo = new Todo(null, "integrationuser", "Integration Test Todo", LocalDate.now().plusDays(1), false);
        testTodo.setUser(testUser);
        todoRepository.save(testTodo);
    }

    @Test
    @WithMockUser(username = "integrationuser")
    void listTodos_ShouldDisplayUserTodos() throws Exception {
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().isOk())
                .andExpect(view().name("listTodos"))
                .andExpect(model().attributeExists("todos"))
                .andExpect(content().string(containsString("Integration Test Todo")))
                .andExpect(content().string(containsString("data-testid=\"todo-assign-button\"")));
    }

    @Test
    @WithMockUser(username = "integrationuser")
    void addTodo_ShouldCreateNewTodo() throws Exception {
        mockMvc.perform(post("/add-todo")
                        .param("description", "New Integration Todo")
                        .param("targetDate", LocalDate.now().plusDays(7).toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list-todos"));

        // Verify the todo was added
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New Integration Todo")));
    }

    @Test
    @WithMockUser(username = "integrationuser")
    void updateTodo_ShouldModifyExistingTodo() throws Exception {
        // First get the update form
        mockMvc.perform(get("/todos/{id}", String.valueOf(testTodo.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("todo"))
                .andExpect(model().attributeExists("todo"));

        // Then submit the update
        mockMvc.perform(put("/todos/{id}", String.valueOf(testTodo.getId()))
                .param("id", String.valueOf(testTodo.getId()))
                .param("description", "Updated Integration Todo")
                .param("targetDate", LocalDate.now().plusDays(10).toString())
                .param("done", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list-todos"));

        // Verify the todo was updated
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Updated Integration Todo")));
    }

    @Test
    @WithMockUser(username = "integrationuser")
    void deleteTodo_ShouldRemoveTodo() throws Exception {
        // Get the todo ID before deleting
        Integer todoId = testTodo.getId();

        // Delete the todo
        mockMvc.perform(delete("/todos/{id}", String.valueOf(todoId)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list-todos"));

        // Verify the todo was deleted using the repository
        assertTrue(todoRepository.findById(todoId).isEmpty(), "Todo should be deleted from the database");

        // Verify the todos list is empty
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("todos", hasSize(0)));
    }

    @Test
    void unauthenticatedAccess_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/list-todos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void accessOtherUserTodo_ShouldBeDenied() {
        try {
                mockMvc.perform(get("/todos/{id}", String.valueOf(testTodo.getId())));
            // If we get here, the test should fail
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Verify that the exception is a ServletException with the correct message
            assertInstanceOf(IllegalStateException.class, e.getCause());
            assertEquals("Not authorized to update this todo", e.getCause().getMessage());
        }
    }
}
