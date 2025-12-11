
package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.Data;

@Data
public class BorrowingStatusUpdateRequest {
    private String status; // BORROWED, RETURNED, OVERDUE
}
