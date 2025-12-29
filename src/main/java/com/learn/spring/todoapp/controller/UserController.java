package com.learn.spring.todoapp.controller;

import com.learn.spring.todoapp.dto.UserRegistrationDto;
import com.learn.spring.todoapp.entity.User;
import com.learn.spring.todoapp.repository.AuthorityRepository;
import com.learn.spring.todoapp.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.learn.spring.todoapp.dto.UserDTO;

@Controller
@Tag(name = "User Management", description = "User authentication, registration, and search functionality")
public class UserController {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/api/users/search")
    @ResponseBody
    @Operation(
        summary = "Search users by username",
        description = "Search for users in the system by their username. Returns a list of matching users with username and email information. " +
                      "Useful for finding users to assign tasks to."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matching users",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))
            )
        ),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @Tag(name = "User Management", description = "User-related operations including search and registration")
    public List<UserDTO> searchUsers(
            @Parameter(description = "Username search query (case-insensitive, supports partial matches)", required = true)
            @RequestParam String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username).stream()
                .map(user -> new UserDTO(user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    @GetMapping("/register")
    @Operation(
        summary = "Show user registration form",
        description = "Displays the registration form page for new users to create an account."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration form page successfully displayed"),
        @ApiResponse(responseCode = "302", description = "If already authenticated, may redirect to home")
    })
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @GetMapping("/login")
    @Operation(
        summary = "Show user login form",
        description = "Displays the login form page for user authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login form page successfully displayed"),
        @ApiResponse(responseCode = "302", description = "If already authenticated, may redirect to home")
    })
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user account",
        description = "Processes the registration form to create a new user account. Validates username uniqueness, password match, " +
                      "and email format. Automatically logs in the user after successful registration."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "User registered successfully, redirects to welcome page"),
        @ApiResponse(responseCode = "400", description = "Validation error (duplicate username, invalid email, password mismatch, etc.)"),
        @ApiResponse(responseCode = "200", description = "Returns to registration form with error messages on validation failure")
    })
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto, BindingResult result, Model model) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "user.confirmPassword.mismatch", "Passwords do not match");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            result.rejectValue("username", "user.username.exists", "Username already exists");
        }

        // Basic email validation (can be enhanced)
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@")) {
            result.rejectValue("email", "user.email.invalid", "Invalid email format");
        }

        if (result.hasErrors()) {
            return "register";
        }

        // Create and save the user entity
        User user = new User(
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail()
        );
        userRepository.save(user);

        // Add ROLE_USER authority
        authorityRepository.addAuthority(userDto.getUsername(), "ROLE_USER");

        // Auto-login after registration
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDto.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/welcome";
    }
}
