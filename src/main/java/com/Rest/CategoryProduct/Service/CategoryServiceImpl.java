package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Repositories.CategoryRepositories;
/*import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepositories categoryRepo;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> findById = categoryRepo.findById(id);

        if (findById.isPresent()){
            return findById.get();
        }
        return null;
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepo.findAll();
    }

    @Override
    public String insertCategory(Category category) {
        Optional<Category> categoryExist = categoryRepo.findByCategoryName(category.getCategoryName());
        if(categoryExist.isPresent()){
            return "Category already exists";
        }
        categoryRepo.save(category);
        return "Data inserted successfully";
    }

    @Override
    public String updateCategory(Category category, Long id) {
        Optional<Category> categoryExist = categoryRepo.findById(id);
        if(!categoryExist.isPresent()){
            return "There is no such category";
        }

        Category category1 = categoryExist.get();
        category1.setCategoryName(category.getCategoryName());
        categoryRepo.save(category1);
        return "Data updated successfully";
    }

    @Override
    public String deleteCategory(Long id) {
        Optional<Category> categoryExist = categoryRepo.findById(id);
        if(!categoryExist.isPresent()){
            return "There is no such category";
        }
        categoryRepo.deleteById(id);
//        resetAutoIncrement();
        return "Category deleted successfully";
    }

//    @Transactional
//    public void resetAutoIncrement(){
//        entityManager.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate();
//    }
}
