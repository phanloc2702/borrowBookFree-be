package com.bookmanagement.bookmanagementbackend.exception;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
@RestControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(exception.getMessage(), null, exception.getErrorCode()));
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult().getAllErrors();
        var errorMesssage = new ArrayList<String>();
        for (var error : errors) {
            errorMesssage.add(error.getDefaultMessage());
        }
        var resultMessagge = String.join(", ", errorMesssage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(resultMessagge, null, null));
    }
}
