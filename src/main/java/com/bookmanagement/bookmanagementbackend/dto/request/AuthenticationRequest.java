package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthenticationRequest {
    private String email;
    private String password;
}

