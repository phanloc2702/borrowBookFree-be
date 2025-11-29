package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.FilterUserRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User createUser(UserCreationRequest request);
    Page<UserResponse> getUsers(FilterUserRequest filterUserRequest);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
