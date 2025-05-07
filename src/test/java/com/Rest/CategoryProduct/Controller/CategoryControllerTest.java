package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;
    @Mock
    private CategoryService categorySer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetCategoryById(){
        Category category = new Category(1l,"Electronics",new ArrayList<>());
        when(categorySer.getCategoryById(1l)).thenReturn(category);

        ResponseEntity<Category> res = categoryController.getCategoryById(1l);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertNotNull(res.getBody());
        assertEquals("Electronics",res.getBody().getCategoryName());
    }

    @Test
    void testGetAllCategory() {
        Category category1 = new Category(1l,"Electronics",new ArrayList<>());
        Category category2 = new Category(1l,"Accessories",new ArrayList<>());
        when(categorySer.getAllCategory()).thenReturn(Arrays.asList(category1,category2));

        ResponseEntity<List<Category>> res = categoryController.getAllCategory();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(2,res.getBody().size());
    }

    @Test
    void testCreateCategory() {
        Category newCategory = new Category(1l,"Toys",new ArrayList<>());
        when(categorySer.insertCategory(newCategory)).thenReturn("Category inserted successfully");

        ResponseEntity<String> res = categoryController.createCategory(newCategory);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("Category inserted successfully", res.getBody());
    }

    @Test
    void testUpdateCategory() {
        Category updatedCat = new Category(1l, "Updated Name", new ArrayList<>());
        when(categorySer.updateCategory(updatedCat,1l)).thenReturn("Category updated successfully");

        ResponseEntity<String> response = categoryController.updateCategory(updatedCat,1l);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category updated successfully", response.getBody());
    }

    @Test
    void testDeleteCategory() {
        when(categorySer.deleteCategory(1l)).thenReturn("Category deleted successfully");

        ResponseEntity<String> response = categoryController.deleteCategory(1l);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category deleted successfully", response.getBody());
    }
}