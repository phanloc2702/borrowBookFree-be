package com.bookmanagement.bookmanagementbackend.repository;

import com.bookmanagement.bookmanagementbackend.dto.response.CategoryResponse;
import com.bookmanagement.bookmanagementbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

}
