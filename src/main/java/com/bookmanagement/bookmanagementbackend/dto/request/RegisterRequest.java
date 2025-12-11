// com.bookmanagement.bookmanagementbackend.dto.request.RegisterRequest
package com.bookmanagement.bookmanagementbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password phải tối thiểu 6 ký tự")
    private String password;

    @NotBlank
    private String fullName;

    @Email
    private String email;
}
