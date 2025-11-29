package com.bookmanagement.bookmanagementbackend.repository;

import com.bookmanagement.bookmanagementbackend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {

}
