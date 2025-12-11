package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.CategoryCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.CategoryUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterCategoryRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;
import com.bookmanagement.bookmanagementbackend.entity.Category;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.mapper.CategoryMapper;
import com.bookmanagement.bookmanagementbackend.repository.CategoryRepository;
import com.bookmanagement.bookmanagementbackend.repository.specification.CategorySpecification;
import com.bookmanagement.bookmanagementbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private  CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponse> getCategories(FilterCategoryRequest filterCategoryRequest) {
        Specification<Category> spec = CategorySpecification.keywordContains(filterCategoryRequest.getKeyword());
        Pageable pageable = PageRequest.of(filterCategoryRequest.getPageNumber(), filterCategoryRequest.getPageSize(), Sort.by("createdAt").descending());
        Page<Category> categories = categoryRepository.findAll(spec, pageable);
        return categories.map(categoryMapper::toCategoryResponse);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoryResponses(categories);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found", ErrorCodeConstant.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(saved);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found", ErrorCodeConstant.CATEGORY_NOT_FOUND));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found", ErrorCodeConstant.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }

}