// src/main/java/com/bookmanagement/bookmanagementbackend/repository/specification/BorrowRequestSpecification.java
package com.bookmanagement.bookmanagementbackend.repository.specification;

import com.bookmanagement.bookmanagementbackend.entity.BorrowRequest;
import com.bookmanagement.bookmanagementbackend.entity.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class BorrowRequestSpecification {

    public static Specification<BorrowRequest> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";

            // join sang user để search name / email
            Join<BorrowRequest, User> userJoin = root.join("user");

            return cb.or(
                    cb.like(cb.lower(userJoin.get("fullName")), pattern),
                    cb.like(cb.lower(userJoin.get("email")), pattern)
            );
        };
    }
}
