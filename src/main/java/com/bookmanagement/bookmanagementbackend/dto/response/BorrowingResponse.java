package com.bookmanagement.bookmanagementbackend.dto.response;

import com.bookmanagement.bookmanagementbackend.entity.Borrowing.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BorrowingResponse {
    private Long id;

    private Long userId;
    private BookResponse book;
    private String userFullName;
    private String userEmail;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Status status;

}
