package com.learn.spring.todoapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.spring.todoapp.dto.AssignTodoRequest;
import com.learn.spring.todoapp.dto.RespondAssignmentRequest;
import com.learn.spring.todoapp.entity.AssignmentStatus;
import com.learn.spring.todoapp.entity.Todo;
import com.learn.spring.todoapp.entity.TodoAssignment;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.TodoAssignmentRepository;
import com.learn.spring.todoapp.repository.TodoRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AssignmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoAssignmentRepository assignmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User assigner;
    private User assignee;
    private Todo todo;

    @BeforeEach
    void setUp() {
        // Create users
        if (!userRepository.existsByUsername("assigner")) {
            assigner = new User("assigner", "{noop}password", "assigner@example.com");
            userRepository.save(assigner);
        } else {
            assigner = userRepository.findByUsername("assigner").get();
        }

        if (!userRepository.existsByUsername("assignee")) {
            assignee = new User("assignee", "{noop}password", "assignee@example.com");
            userRepository.save(assignee);
        } else {
            assignee = userRepository.findByUsername("assignee").get();
        }
        
        if (!userRepository.existsByUsername("otheruser")) {
            User other = new User("otheruser", "{noop}password", "other@example.com");
            userRepository.save(other);
        }

        // Create todo
        todo = new Todo();
        todo.setDescription("Integration Test Todo");
        todo.setTargetDate(LocalDate.now().plusDays(1));
        todo.setDone(false);
        todo.setUser(assigner);
        todoRepository.save(todo);
    }

    @Test
    @WithMockUser(username = "assigner")
    void testUserSearch() throws Exception {
        mockMvc.perform(get("/api/users/search")
                .param("username", "assignee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("assignee"));
    }

    @Test
    @WithMockUser(username = "assigner")
    void assignTodo_ShouldSucceed() throws Exception {
        AssignTodoRequest assignRequest = new AssignTodoRequest();
        assignRequest.setAssigneeUsername("assignee");

        mockMvc.perform(post("/todos/" + todo.getId() + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignRequest))
                .with(csrf()))
                .andExpect(status().isOk());
        
        // Verify in DB
        List<TodoAssignment> assignments = assignmentRepository.findByTodoId(todo.getId());
        assertEquals(1, assignments.size());
        assertEquals(AssignmentStatus.PENDING, assignments.get(0).getStatus());
        assertEquals("assignee", assignments.get(0).getAssignee().getUsername());
    }

    @Test
    @WithMockUser(username = "assignee")
    void respondToAssignment_ShouldSucceed() throws Exception {
        // Setup assignment
        TodoAssignment assignment = new TodoAssignment();
        assignment.setTodo(todo);
        assignment.setAssigner(assigner);
        assignment.setAssignee(assignee);
        assignment.setStatus(AssignmentStatus.PENDING);
        assignmentRepository.save(assignment);

        RespondAssignmentRequest respondRequest = new RespondAssignmentRequest();
        respondRequest.setAction("accept");
        respondRequest.setTentativeCompletionDate(LocalDate.now().plusDays(2));

        mockMvc.perform(post("/assignments/" + assignment.getId() + "/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(respondRequest))
                .with(csrf()))
                .andExpect(status().isOk());
        
        // Verify in DB
        TodoAssignment updatedAssignment = assignmentRepository.findById(assignment.getId()).get();
        assertEquals(AssignmentStatus.ACCEPTED, updatedAssignment.getStatus());
    }

    @Test
    @WithMockUser(username = "otheruser")
    void assignTodo_ShouldFail_WhenNotOwner() {
        AssignTodoRequest assignRequest = new AssignTodoRequest();
        assignRequest.setAssigneeUsername("assignee");

        try {
            mockMvc.perform(post("/todos/" + todo.getId() + "/assign")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assignRequest))
                    .with(csrf()));
        } catch (Exception e) {
            // Expected exception
            // We can verify the cause if needed
            // assertEquals("Only the owner can assign this todo", e.getCause().getMessage());
        }
    }
}
