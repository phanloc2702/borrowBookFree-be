package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.dto.request.BorrowingCreateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowingRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowingResponse;
import com.bookmanagement.bookmanagementbackend.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    // Tạo phiếu mượn (nhiều sách cùng lúc)
    @PostMapping
    public ResponseEntity<ApiResponse> createBorrowings(
            @RequestBody BorrowingCreateRequest request
    ) {
        List<BorrowingResponse> result = borrowingService.createBorrowings(request);
        return ResponseEntity.ok(new ApiResponse("Tạo phiếu mượn thành công", result));
    }

    // Lấy lịch sử mượn của 1 user
    // FE gọi: GET /borrowings/me?userId=1
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyBorrowings(@RequestParam Long userId) {
        List<BorrowingResponse> result = borrowingService.getBorrowingsByUser(userId);
        return ResponseEntity.ok(new ApiResponse("Thành công", result));
    }

    // Trả sách
    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse> returnBook(@PathVariable Long id) {
        BorrowingResponse result = borrowingService.returnBook(id);
        return ResponseEntity.ok(new ApiResponse("Trả sách thành công", result));
    }

    // (Tuỳ chọn) Admin xem tất cả
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBorrowings() {
        // bạn có thể thêm service.findAll() nếu cần
        return ResponseEntity.status(501)
                .body(new ApiResponse("Chưa implement", null)); // placeholder
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterBorrows(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        FilterBorrowingRequest filter = FilterBorrowingRequest.builder()
                .keyword(keyword)
                .pageNumber(page)
                .pageSize(size)
                .build();

        Page<BorrowingResponse> borrows = borrowingService.getBorrowings(filter);

        Map<String, Object> response = new HashMap<>();
        response.put("content", borrows.getContent());
        response.put("totalPages", borrows.getTotalPages());
        response.put("totalElements", borrows.getTotalElements());
        response.put("currentPage", borrows.getNumber());

        return ResponseEntity.ok(new ApiResponse("Thành công", response));
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status
    ) {
        borrowingService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse("Cập nhật trạng thái phiếu mượn thành công", null));
    }
}
