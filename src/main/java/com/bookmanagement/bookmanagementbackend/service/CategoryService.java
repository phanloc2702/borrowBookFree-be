package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.CategoryCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.CategoryUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterCategoryRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    Page<CategoryResponse> getCategories(FilterCategoryRequest filterCategoryRequest);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryCreationRequest request);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
    void deleteCategory(Long id);
}