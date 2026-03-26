package com.usermanagement.controller;

import com.usermanagement.dto.ApiResponse;
import com.usermanagement.dto.PagedResponse;
import com.usermanagement.dto.UserCreateRequest;
import com.usermanagement.dto.UserResponse;
import com.usermanagement.dto.UserUpdateRequest;
import com.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('users:create')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        logger.info("Creating user: {}", request.getUsername());
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:read') or #id == principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        logger.info("Fetching user by id: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('users:list')")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        logger.info("Fetching all users - page: {}, size: {}", page, size);
        PagedResponse<UserResponse> users = userService.getAllUsers(page, size, sortBy, sortDirection);
        ApiResponse.Meta meta = new ApiResponse.Meta(users.getPage(), users.getSize(), users.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(users, meta));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('users:update') or #id == principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        logger.info("Updating user: {}", id);
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        logger.info("Deleting user: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable UUID id) {
        logger.info("Activating user: {}", id);
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable UUID id) {
        logger.info("Deactivating user: {}", id);
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching current user");
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
