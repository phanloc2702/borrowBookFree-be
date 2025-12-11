package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterCategoryRequest {
    private String keyword;
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
}