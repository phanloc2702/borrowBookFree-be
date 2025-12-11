package com.bookmanagement.bookmanagementbackend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {
    private long totalBooks;
    private long totalUsers;
    private long borrowingCount; // đang mượn (BORROWED + OVERDUE)
    private long overdueCount;   // quá hạn
}
