package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    Page<BookResponse> getBooks(FilterBookRequest filterBookRequest);
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
}
