// src/main/java/com/bookmanagement/bookmanagementbackend/repository/BorrowRequestRepository.java
package com.bookmanagement.bookmanagementbackend.repository;

import com.bookmanagement.bookmanagementbackend.dto.request.FilterBorrowRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BorrowRequestResponse;
import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest;
import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long>, JpaSpecificationExecutor<BorrowRequest> {

    List<BorrowRequest> findAllByOrderByCreatedAtDesc();

    List<BorrowRequest> findByStatusOrderByCreatedAtDesc(Status status);

    List<BorrowRequest> findByUserIdOrderByCreatedAtDesc(Long userId);



}
