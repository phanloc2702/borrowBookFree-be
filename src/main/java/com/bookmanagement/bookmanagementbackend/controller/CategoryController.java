package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.dto.request.CategoryCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.CategoryUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterCategoryRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;
import com.bookmanagement.bookmanagementbackend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories(){
        return ResponseEntity.ok(new ApiResponse("Thành công", categoryService.getAllCategories()));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        FilterCategoryRequest filter = FilterCategoryRequest.builder()
                .keyword(keyword)
                .pageNumber(page)
                .pageSize(size)
                .build();

        Page<CategoryResponse> categories = categoryService.getCategories(filter);

        Map<String, Object> response = new HashMap<>();
        response.put("content", categories.getContent());
        response.put("totalPages", categories.getTotalPages());
        response.put("totalElements", categories.getTotalElements());
        response.put("currentPage", categories.getNumber());

        return ResponseEntity.ok(new ApiResponse("Thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Thành công", category));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryCreationRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.ok(new ApiResponse("Tạo thể loại thành công", category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request) {
        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(new ApiResponse("Cập nhật thể loại thành công", category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse("Xoá thể loại thành công", null));
    }
}