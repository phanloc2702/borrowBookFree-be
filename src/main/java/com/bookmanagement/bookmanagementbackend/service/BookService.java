package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.BookCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.BookUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    Page<BookResponse> getBooks(FilterBookRequest filterBookRequest);
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
    BookResponse createBook(BookCreationRequest request, MultipartFile cover);
    BookResponse updateBook(Long id, BookUpdateRequest request, MultipartFile cover);
    void deleteBook(Long id);
}
