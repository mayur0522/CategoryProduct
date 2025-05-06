package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Category;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface CategoryService {

    public Category getCategoryById(Long id);

    public List<Category> getAllCategory();

    public String insertCategory(Category category);

    public String updateCategory(Category category,Long id);

    public String deleteCategory(Long id);

}
