package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories(){
        return ResponseEntity.ok(new ApiResponse("Thành công", categoryService.getAllCategories()));
    }
}
