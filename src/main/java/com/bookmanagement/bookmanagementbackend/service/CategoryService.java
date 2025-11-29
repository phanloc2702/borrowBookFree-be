package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}
