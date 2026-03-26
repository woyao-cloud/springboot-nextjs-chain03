package com.usermanagement.service;

import com.usermanagement.dto.PagedResponse;
import com.usermanagement.dto.UserCreateRequest;
import com.usermanagement.dto.UserResponse;
import com.usermanagement.dto.UserUpdateRequest;
import com.usermanagement.entity.Role;
import com.usermanagement.entity.User;
import com.usermanagement.exception.DuplicateResourceException;
import com.usermanagement.exception.ResourceNotFoundException;
import com.usermanagement.mapper.UserMapper;
import com.usermanagement.repository.RoleRepository;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;
    private Role userRole;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userRole = new Role();
        userRole.setId(UUID.randomUUID());
        userRole.setName("USER");
        userRole.setDescription("Standard user");

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setFirstName("Test");
        userResponse.setLastName("User");
        userResponse.setFullName("Test User");
        userResponse.setIsActive(true);
        userResponse.setRoles(Collections.singleton("USER"));
    }

    @Test
    void createUser_Success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123");
        request.setFirstName("Test");
        request.setLastName("User");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPassword");
        when(roleRepository.findByIsDefaultTrue()).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getUserByUsername_Success() {
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("testuser"));
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        PagedResponse<UserResponse> result = userService.getAllUsers(0, 20, null, "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void updateUser_Success() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Name");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        updatedUser.setIsActive(true);
        updatedUser.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        when(userRepository.findByIdWithRoles(userId)).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            user.setFirstName("Updated");
            user.setLastName("Name");
            return null;
        }).when(userMapper).updateEntityFromRequest(request, user);
        when(userRepository.save(user)).thenReturn(updatedUser);

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(userId);
        updatedResponse.setUsername("testuser");
        updatedResponse.setFirstName("Updated");
        updatedResponse.setLastName("Name");
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUser(userId, request);

        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void activateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> userService.activateUser(userId));
        assertTrue(user.getIsActive());
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> userService.deactivateUser(userId));
        assertFalse(user.getIsActive());
    }

    @Test
    void getCurrentUser_Success() {
        when(userRepository.findByUsernameWithRoles("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getCurrentUser("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
}
