package com.bookmanagement.bookmanagementbackend.mapper;

import com.bookmanagement.bookmanagementbackend.dto.response.BookResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.UserResponse;
import com.bookmanagement.bookmanagementbackend.entity.Book;
import com.bookmanagement.bookmanagementbackend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    BookResponse toBookResponse(Book book);

    default List<BookResponse> toBookResponseList(List<Book> books) {
        return books.stream()
                .map(this::toBookResponse)
                .toList();
    }
    default Page<BookResponse> toBookResponsePage(Page<Book> books) {
        return books.map(this::toBookResponse);
    }
}
