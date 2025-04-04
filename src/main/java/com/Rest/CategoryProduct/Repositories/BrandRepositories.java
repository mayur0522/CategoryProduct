package com.Rest.CategoryProduct.Repositories;

import com.Rest.CategoryProduct.Entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepositories extends JpaRepository<Brand, Long> {
    Optional<Brand> findByBrandName(String categoryName);

//    @Transactional
//    @Modifying
//    @Query(value = "INSERT INTO brand (brand_name, category_id) VALUES (:brandName, :categoryId)", nativeQuery = true)
//    void insertBrand(@Param("brandName") String brandName, @Param("categoryId") Long categoryId);
}
