package com.bookmanagement.bookmanagementbackend.mapper;

import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;
import com.bookmanagement.bookmanagementbackend.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
        CategoryResponse toCategoryResponse(Category category);
        List<CategoryResponse> toCategoryResponses(List<Category> categories);
}
