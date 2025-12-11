// src/main/java/com/bookmanagement/bookmanagementbackend/service/BorrowRequestService.java
package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.BorrowRequestCreateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.CreateBorrowRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowRequestResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BorrowRequestService {

    List<BorrowRequestResponse> getAll(String status);

    BorrowRequestResponse getById(Long id);

    List<BorrowRequestResponse> getByUser(Long userId);

    BorrowRequestResponse updateStatus(Long id, String newStatus);
    Page<BorrowRequestResponse> getBorrowRequests(FilterBorrowRequest filter);
    BorrowRequestResponse create(BorrowRequestCreateRequest request);
}
