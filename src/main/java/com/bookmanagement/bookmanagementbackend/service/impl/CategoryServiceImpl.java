package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;
import com.bookmanagement.bookmanagementbackend.entity.Category;
import com.bookmanagement.bookmanagementbackend.mapper.CategoryMapper;
import com.bookmanagement.bookmanagementbackend.repository.CategoryRepository;
import com.bookmanagement.bookmanagementbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private  CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoryResponses(categories);
    }

}
