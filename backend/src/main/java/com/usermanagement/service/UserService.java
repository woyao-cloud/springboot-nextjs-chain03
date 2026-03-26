package com.usermanagement.service;

import com.usermanagement.dto.PagedResponse;
import com.usermanagement.dto.UserCreateRequest;
import com.usermanagement.dto.UserResponse;
import com.usermanagement.dto.UserUpdateRequest;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(UUID id);

    UserResponse getUserByUsername(String username);

    PagedResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection);

    UserResponse updateUser(UUID id, UserUpdateRequest request);

    void deleteUser(UUID id);

    void activateUser(UUID id);

    void deactivateUser(UUID id);

    UserResponse getCurrentUser(String username);
}
