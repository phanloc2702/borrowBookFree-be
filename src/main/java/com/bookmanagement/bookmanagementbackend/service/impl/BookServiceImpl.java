package com.bookmanagement.bookmanagementbackend.service.impl;

import com.bookmanagement.bookmanagementbackend.dto.request.FilterBookRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.FilterUserRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.User;
import com.bookmanagement.bookmanagementbackend.exception.BusinessException;
import com.bookmanagement.bookmanagementbackend.exception.ErrorCodeConstant;
import com.bookmanagement.bookmanagementbackend.mapper.BookMapper;
import com.bookmanagement.bookmanagementbackend.repository.BookRepository;
import com.bookmanagement.bookmanagementbackend.repository.specification.BookSpecification;
import com.bookmanagement.bookmanagementbackend.repository.specification.UserSpecification;
import com.bookmanagement.bookmanagementbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    @Autowired
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
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
}
