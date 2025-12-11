package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.dashboard.DashboardSummary;
import com.bookmanagement.bookmanagementbackend.dto.dashboard.RecentActivityDto;
import com.bookmanagement.bookmanagementbackend.entity.Borrowing;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.dto.ApiResponse; // sửa import theo project của bạn
import com.bookmanagement.bookmanagementbackend.repository.BookRepository;
import com.bookmanagement.bookmanagementbackend.repository.UserRepository;
import com.bookmanagement.bookmanagementbackend.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
// Nếu bạn có security phân quyền, có thể thêm:
// @PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getSummary() {
        long totalBooks = bookRepository.count();
        long totalUsers = userRepository.count();

        long borrowed = borrowingRepository.countByStatus(Borrowing.Status.BORROWED);
        long overdue = borrowingRepository.countByStatus(Borrowing.Status.OVERDUE);

        long borrowingCount = borrowed + overdue;

        DashboardSummary summary = DashboardSummary.builder()
                .totalBooks(totalBooks)
                .totalUsers(totalUsers)
                .borrowingCount(borrowingCount)
                .overdueCount(overdue)
                .build();

        return ResponseEntity.ok(new ApiResponse("Lấy thống kê thành công", summary));
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<ApiResponse> getRecentActivities(
            @RequestParam(defaultValue = "5") int limit
    ) {
        if (limit <= 0) limit = 5;
        if (limit > 50) limit = 50; // tránh lấy quá nhiều

        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Borrowing> page = borrowingRepository.findAll(pageable);

        List<RecentActivityDto> activities = page.getContent().stream()
                .map(b -> {
                    String action;
                    // Map từ status -> action cho FE
                    if (b.getStatus() == Borrowing.Status.RETURNED) {
                        action = "RETURN";
                    } else if (b.getStatus() == Borrowing.Status.OVERDUE) {
                        action = "OVERDUE";
                    } else {
                        action = "BORROW";
                    }

                    User user = b.getUser();
                    Book book = b.getBook();

                    return RecentActivityDto.builder()
                            .id(b.getId())
                            .userName(user != null ? user.getFullName() : null)
                            .bookTitle(book != null ? book.getTitle() : null)
                            .action(action)
                            .createdAt(b.getCreatedAt())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(new ApiResponse("Lấy hoạt động gần đây thành công", activities));
    }
}
