package com.Rest.CategoryProduct.Repositories;

import com.Rest.CategoryProduct.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepositories extends JpaRepository<Product,Long> {
    Optional<Product> findByProductName(String categoryName);
}
