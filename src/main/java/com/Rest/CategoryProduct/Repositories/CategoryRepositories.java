package com.Rest.CategoryProduct.Repositories;

import com.Rest.CategoryProduct.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepositories extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
}
