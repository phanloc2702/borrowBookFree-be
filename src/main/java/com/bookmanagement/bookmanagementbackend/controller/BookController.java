package com.bookmanagement.bookmanagementbackend.controller;

import com.bookmanagement.bookmanagementbackend.dto.ApiResponse;
import com.bookmanagement.bookmanagementbackend.dto.request.BookCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.BookUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;

import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;

import com.bookmanagement.bookmanagementbackend.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getBooks(){
        return bookService.getAllBooks();
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> filterBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        FilterBookRequest filter = FilterBookRequest.builder()
                .keyword(keyword)
                .pageNumber(page)
                .pageSize(size)
                .build();

        Page<BookResponse> books = bookService.getBooks(filter);

        Map<String, Object> response = new HashMap<>();
        response.put("content", books.getContent());
        response.put("totalPages", books.getTotalPages());
        response.put("totalElements", books.getTotalElements());
        response.put("currentPage", books.getNumber());

        return ResponseEntity.ok(new ApiResponse("Thành công", response));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBookById(@PathVariable Long id){
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(new ApiResponse("Thành công", book));
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createBook(
            @Valid @ModelAttribute BookCreationRequest request,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) {
        BookResponse book = bookService.createBook(request, cover);
        return ResponseEntity.ok(new ApiResponse("Tạo sách thành công", book));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute BookUpdateRequest request,
            @RequestPart(value = "cover", required = false) MultipartFile cover
    ) {
        BookResponse book = bookService.updateBook(id, request, cover);
        return ResponseEntity.ok(new ApiResponse("Cập nhật sách thành công", book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Xoá sách thành công", null));
    }
}
