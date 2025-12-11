package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.BorrowingCreateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.BorrowingStatusUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowingRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowingResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BorrowingService {

    List<BorrowingResponse> createBorrowings(BorrowingCreateRequest request);

    List<BorrowingResponse> getBorrowingsByUser(Long userId);

    BorrowingResponse returnBook(Long borrowingId);

    BorrowingResponse updateStatus(Long id, String newStatus);
//    // admin filter xem danh sách
    Page<BorrowingResponse> getBorrowings(FilterBorrowingRequest filter);
//
//
//    BorrowingResponse getBorrowing(Long id);
//
//    BorrowingResponse updateStatus(Long id, BorrowingStatusUpdateRequest request);
}
