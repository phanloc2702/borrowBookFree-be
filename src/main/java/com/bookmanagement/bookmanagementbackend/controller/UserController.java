package com.bookmanagement.bookmanagementbackend.controller;


import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterUserRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.UserUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserCreationRequest request){
        User user = userService.createUser(request);
        return ResponseEntity.ok(new ApiResponse("User created successfully", user));
    }
//    @GetMapping("/filter")
//    public ResponseEntity<ApiResponse> filterUsers(FilterUserRequest filterUserRequest){
//        Page<UserResponse> users = userService.getUsers(filterUserRequest);
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", users.getContent());
//        response.put("totalPages", users.getTotalPages());
//        response.put("totalElements", users.getTotalElements());
//        response.put("currentPage", users.getNumber());
//        return ResponseEntity.ok(new ApiResponse("Thành công", response));
//    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        FilterUserRequest filter = FilterUserRequest.builder()
                .keyword(keyword)
                .pageNumber(page)
                .pageSize(size)
                .build();

        Page<UserResponse> users = userService.getUsers(filter);

        Map<String, Object> response = new HashMap<>();
        response.put("content", users.getContent());
        response.put("totalPages", users.getTotalPages());
        response.put("totalElements", users.getTotalElements());
        response.put("currentPage", users.getNumber());

        return ResponseEntity.ok(new ApiResponse("Thành công", response));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id){
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse("Thành công", user));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id,  @RequestBody UserUpdateRequest request){
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse("User updated successfully", user));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse("User deleted successfully", null));
    }
}
