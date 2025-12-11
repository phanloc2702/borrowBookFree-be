
package com.bookmanagement.bookmanagementbackend.dto.request;

import lombok.Data;

@Data
public class BorrowRequestStatusUpdateRequest {
    private String status; // PENDING, APPROVED, REJECTED, CANCELED
}
