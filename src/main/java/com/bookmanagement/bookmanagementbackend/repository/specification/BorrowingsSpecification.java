package com.bookmanagement.bookmanagementbackend.repository.specification;

import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.Borrowing;
import org.springframework.data.jpa.domain.Specification;

public class BorrowingsSpecification {
    public static Specification<Borrowing> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.join("book").get("title")), pattern),
                    cb.like(cb.lower(root.join("user").get("fullName")), pattern),
                    cb.like(cb.lower(root.join("user").get("email")), pattern),
                    cb.like(cb.lower(root.get("status").as(String.class)), pattern)
            );
        };
    }
}
