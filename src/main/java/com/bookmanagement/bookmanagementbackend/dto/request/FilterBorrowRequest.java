
package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterBorrowRequest {
    private String keyword;    // tìm theo tên / email user
    private Integer pageNumber;
    private Integer pageSize;
}
