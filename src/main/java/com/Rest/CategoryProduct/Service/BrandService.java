package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Brand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BrandService {

    public Brand getBrandById(Long id);

    public List<Brand> getAllBrand();

    public String insertBrand(Brand brand);
//    public String insertBrand(String name,Long categoryId);

    public String updateBrand(Brand brand,Long id);

    public String deleteBrand(Long id);
}
