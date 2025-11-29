package com.bookmanagement.bookmanagementbackend.repository.specification;

import com.bookmanagement.bookmanagementbackend.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("author")), pattern),
                    cb.like(cb.lower(root.join("category").get("name")), pattern)
            );
        };
    }
}
