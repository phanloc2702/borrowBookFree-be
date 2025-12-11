package com.bookmanagement.bookmanagementbackend.repository;

import com.bookmanagement.bookmanagementbackend.entity.Borrowing;
import com.bookmanagement.bookmanagementbackend.entity.Borrowing.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> , JpaSpecificationExecutor<Borrowing> {

    List<Borrowing> findByUserIdOrderByBorrowDateDesc(Long userId);
    long countByStatus(Borrowing.Status status);
//    List<Borrowing> findByBorrowRequestId(Long borrowRequestId);
//  long countByStatus(Borrowing.Status status);
//    List<Borrowing> findByUserIdAndStatusOrderByBorrowDateDesc(Long userId, Status status);
}
