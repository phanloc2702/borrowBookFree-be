package com.bookmanagement.bookmanagementbackend.dto.request;

import com.bookmanagement.bookmanagementbackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String email;
    private String fullName;
    private User.Role role;
    private String password;
}

