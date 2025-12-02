package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.BookCreationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.BookUpdateRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterUserRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.Category;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.mapper.BookMapper;
import com.bookmanagement.bookmanagementbackend.repository.BookRepository;
import com.bookmanagement.bookmanagementbackend.repository.CategoryRepository;
import com.bookmanagement.bookmanagementbackend.repository.specification.BookSpecification;
import com.bookmanagement.bookmanagementbackend.repository.specification.UserSpecification;
import com.bookmanagement.bookmanagementbackend.service.BookService;
import com.bookmanagement.bookmanagementbackend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    @Autowired
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    public Page<BookResponse> getBooks(FilterBookRequest filterBookRequest){
        Specification<Book> spec = BookSpecification.keywordContains(filterBookRequest.getKeyword());
        Pageable pageable  = PageRequest.of(filterBookRequest.getPageNumber(), filterBookRequest.getPageSize(), Sort.by("createdAt").descending());
        Page<Book> books = bookRepository.findAll(spec, pageable);
        return bookMapper.toBookResponsePage(books);
    }

    public List<BookResponse> getAllBooks(){
        List<Book> books = bookRepository.findAll();
        return bookMapper.toBookResponseList(books);
    }
    public BookResponse getBookById(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book not found", ErrorCodeConstant.BOOK_NOT_FOUND));
        return bookMapper.toBookResponse(book);
    }

    public BookResponse createBook(BookCreationRequest request, MultipartFile cover) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found", ErrorCodeConstant.CATEGORY_NOT_FOUND));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPublicationYear(request.getPublicationYear());
        book.setQuantity(request.getQuantity());
        book.setAvailableQuantity(request.getQuantity());
        book.setCategory(category);
        String coverUrl = request.getCoverUrl();
        if (cover != null && !cover.isEmpty()) {
            coverUrl = storeCoverAndGetUrl(cover);
        }

        if (StringUtils.hasText(coverUrl)) {
            book.setCoverUrl(coverUrl);
        }

        Book saved = bookRepository.save(book);
        return bookMapper.toBookResponse(saved);
    }

    public BookResponse updateBook(Long id, BookUpdateRequest request, MultipartFile cover) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book not found", ErrorCodeConstant.BOOK_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found", ErrorCodeConstant.CATEGORY_NOT_FOUND));

        int borrowed = Math.max(0, book.getQuantity() - book.getAvailableQuantity());

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPublicationYear(request.getPublicationYear());
        book.setQuantity(request.getQuantity());
        book.setAvailableQuantity(Math.max(0, request.getQuantity() - borrowed));
        book.setCategory(category);
        String newCoverUrl = book.getCoverUrl();
        if (cover != null && !cover.isEmpty()) {
            String oldCoverUrl = book.getCoverUrl();
            newCoverUrl = storeCoverAndGetUrl(cover);
            deleteCoverFile(oldCoverUrl);
        }  else if (StringUtils.hasText(request.getCoverUrl()) && !request.getCoverUrl().equals(book.getCoverUrl())) {
            deleteCoverFile(book.getCoverUrl());
            newCoverUrl = request.getCoverUrl();
        }

        if (StringUtils.hasText(newCoverUrl)) {
            book.setCoverUrl(newCoverUrl);
        }

        Book saved = bookRepository.save(book);
        return bookMapper.toBookResponse(saved);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book not found", ErrorCodeConstant.BOOK_NOT_FOUND));

        deleteCoverFile(book.getCoverUrl());
        bookRepository.delete(book);
    }

    private String storeCoverAndGetUrl(MultipartFile cover) {
        try {
            String fileName = fileStorageService.storeFile(cover);
            return fileStorageService.getFileUrl(fileName);
        } catch (IOException e) {
            throw new BusinessException("Cannot store cover image", ErrorCodeConstant.FILE_UPLOAD_FAILED);
        }
    }

    private void deleteCoverFile(String coverUrl) {
        if (coverUrl == null || coverUrl.isEmpty()) {
            return;
        }

        int lastSlash = coverUrl.lastIndexOf('/') + 1;
        String fileName = lastSlash > 0 ? coverUrl.substring(lastSlash) : coverUrl;
        if (!fileName.isBlank()) {
            fileStorageService.deleteFile(fileName);
        }
    }
}
