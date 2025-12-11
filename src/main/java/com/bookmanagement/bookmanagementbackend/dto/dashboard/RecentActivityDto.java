package com.bookmanagement.bookmanagementbackend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDto {
    private Long id;
    private String userName;
    private String bookTitle;
    private String action;       // "BORROW" | "RETURN" | "OVERDUE"
    private LocalDateTime createdAt;
}
