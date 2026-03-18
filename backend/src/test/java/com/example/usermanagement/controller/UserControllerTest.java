package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setFirstName("Test");
        userResponse.setLastName("User");
        userResponse.setFullName("Test User");
        userResponse.setIsActive(true);
        userResponse.setIsVerified(false);
        userResponse.setRoles(Collections.singleton("USER"));
    }

    @Test
    @WithMockUser(authorities = "users:create")
    void createUser_Success() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("Password123");
        request.setFirstName("New");
        request.setLastName("User");

        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService).createUser(any(UserCreateRequest.class));
    }

    @Test
    @WithMockUser(authorities = "users:create")
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("ab"); // Too short
        request.setEmail("invalid-email");
        request.setPassword("short"); // Too short

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "users:read")
    void getUserById_Success() throws Exception {
        when(userService.getUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getUserById(userId);
    }

    @Test
    @WithMockUser(authorities = "users:list")
    void getAllUsers_Success() throws Exception {
        PagedResponse<UserResponse> pagedResponse = new PagedResponse<>(
                Arrays.asList(userResponse),
                0, 20, 1, 1, true, true
        );

        when(userService.getAllUsers(0, 20, null, "desc")).thenReturn(pagedResponse);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));

        verify(userService).getAllUsers(0, 20, null, "desc");
    }

    @Test
    @WithMockUser(authorities = "users:update")
    void updateUser_Success() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Name");

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(userId);
        updatedResponse.setUsername("testuser");
        updatedResponse.setFirstName("Updated");
        updatedResponse.setLastName("Name");

        when(userService.updateUser(eq(userId), any(UserUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Updated"));

        verify(userService).updateUser(eq(userId), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockUser(authorities = "users:delete")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(authorities = "users:update")
    void activateUser_Success() throws Exception {
        doNothing().when(userService).activateUser(userId);

        mockMvc.perform(post("/users/{id}/activate", userId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).activateUser(userId);
    }

    @Test
    @WithMockUser(authorities = "users:update")
    void deactivateUser_Success() throws Exception {
        doNothing().when(userService).deactivateUser(userId);

        mockMvc.perform(post("/users/{id}/deactivate", userId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService).deactivateUser(userId);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCurrentUser_Success() throws Exception {
        when(userService.getCurrentUser("testuser")).thenReturn(userResponse);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getCurrentUser("testuser");
    }

    @Test
    void getUserById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isUnauthorized());
    }
}
