package com.bookmanagement.bookmanagementbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String coverUrl;
    private Integer quantity;
    private Integer availableQuantity;
    private Integer publicationYear;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;

}
