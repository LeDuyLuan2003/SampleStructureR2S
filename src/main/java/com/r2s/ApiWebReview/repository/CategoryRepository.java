package com.r2s.ApiWebReview.repository;

import com.r2s.ApiWebReview.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}