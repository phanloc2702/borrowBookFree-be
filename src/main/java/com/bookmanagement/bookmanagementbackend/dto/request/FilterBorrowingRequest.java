
package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterBorrowingRequest {
    private String keyword;
    private Integer pageNumber=0;
    private Integer pageSize=10;
}
