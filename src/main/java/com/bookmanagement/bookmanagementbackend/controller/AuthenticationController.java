package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.dto.request.AuthenticationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.ChangePasswordRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.IntrospectRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.RegisterRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.AuthenticationResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.IntrospectResponse;
import com.bookmanagement.bookmanagementbackend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(new ApiResponse("Login successful", response, null));
    }
    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse response = authenticationService.introspect(request);
        return ResponseEntity.ok(new ApiResponse("Login successful", response, null));
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(new ApiResponse("Register successful", response, null));
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseEntity.ok(
                new ApiResponse("Đổi mật khẩu thành công", null)
        );
    }
}
