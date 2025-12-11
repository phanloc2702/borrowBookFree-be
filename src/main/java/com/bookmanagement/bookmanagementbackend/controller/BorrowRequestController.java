// src/main/java/com/bookmanagement/bookmanagementbackend/controller/BorrowRequestController.java
package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.dto.request.BorrowRequestCreateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.BorrowRequestStatusUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.CreateBorrowRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowRequestResponse;
import com.bookmanagement.bookmanagementbackend.service.BorrowRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow-requests")
@RequiredArgsConstructor
public class BorrowRequestController {

    private final BorrowRequestService borrowRequestService;

    // Admin: danh sách đơn (có thể filter theo status)
    // GET /borrow-requests?status=PENDING
    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam(required = false) String status) {
        List<BorrowRequestResponse> result = borrowRequestService.getAll(status);
        return ResponseEntity.ok(new ApiResponse("Thành công", result));
    }

    // User: xem các đơn của mình
    // GET /borrow-requests/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getByUser(@PathVariable Long userId) {
        List<BorrowRequestResponse> result = borrowRequestService.getByUser(userId);
        return ResponseEntity.ok(new ApiResponse("Thành công", result));
    }

    // Chi tiết 1 đơn
    // GET /borrow-requests/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        BorrowRequestResponse result = borrowRequestService.getById(id);
        return ResponseEntity.ok(new ApiResponse("Thành công", result));
    }

    // Admin: cập nhật trạng thái đơn (PENDING -> APPROVED / REJECTED / CANCELED)
    // PATCH /borrow-requests/{id}/status
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<ApiResponse> updateStatus(
//            @PathVariable Long id,
//            @RequestBody BorrowRequestStatusUpdateRequest request
//    ) {
//        BorrowRequestResponse result = borrowRequestService.updateStatus(id, request.getStatus());
//        return ResponseEntity.ok(new ApiResponse("Cập nhật trạng thái đơn thành công", result));
//    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterBorrowRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        FilterBorrowRequest filter = FilterBorrowRequest.builder()
                .keyword(keyword)
                .pageNumber(page)
                .pageSize(size)
                .build();

        Page<BorrowRequestResponse> result = borrowRequestService.getBorrowRequests(filter);

        Map<String, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("totalPages", result.getTotalPages());
        data.put("totalElements", result.getTotalElements());
        data.put("currentPage", result.getNumber());

        return ResponseEntity.ok(new ApiResponse("Thành công", data));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status   // 👈 tên param "status"
    ) {
        borrowRequestService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse("Cập nhật trạng thái thành công", null));
    }
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody BorrowRequestCreateRequest request) {
        BorrowRequestResponse res = borrowRequestService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Tạo yêu cầu mượn thành công", res));
    }
}
