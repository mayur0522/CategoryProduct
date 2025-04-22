package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Repositories.CategoryRepositories;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepositories categoryRepositories;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    List<Category> mockCategories;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        // Create Products
        Product p1= new Product(1,"MacBook air", null);
        Product p2= new Product(2,"Iphone 16 pro", null);
        Product p3= new Product(3,"Samsung S24", null);

        // Create brands
        Brand Apple = new Brand(1l,"Apple",null,new ArrayList<>(List.of(p1,p2)));
        Brand Samsung = new Brand(2l, "Samsung", null, new ArrayList<>(List.of(p3)));
        Brand Adidas = new Brand(3l, "Adidas", null, new ArrayList<>());

        // create category
        Category Electronics = new Category(1l,"Electronics", new ArrayList<>(List.of(Apple,Samsung)));
        Category Clothing = new Category(1l,"Clothing", new ArrayList<>(List.of(Adidas)));

        //Product is linked to brand
        p1.setBrand(Apple);
        p2.setBrand(Apple);
        p3.setBrand(Adidas);

        // category is linked to brand
        Apple.setCategory(Electronics);
        Samsung.setCategory(Electronics);
        Adidas.setCategory(Clothing);

        // Save the mock list
        mockCategories = List.of(Electronics, Clothing);
    }

    @Test
    @DisplayName("It should return all the categories from repository")
    void testAllCategories(){
//        Do not use like this
//        when(categoryService.getAllCategory()).thenReturn(mockCategories);

//        as we are testing service layer use like this
        when(categoryRepositories.findAll()).thenReturn(mockCategories);

        List<Category> result = categoryService.getAllCategory();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,result.size());
        Assertions.assertEquals("Electronics", result.get(0).getCategoryName());
        verify(categoryRepositories, times(1)).findAll();
    }

    @Test
    @DisplayName("Get the name of category using id")
    void testGetCategoryById(){
        Category category = mockCategories.get(0);
        when(categoryRepositories.findById(0L)).thenReturn(java.util.Optional.of(category));

        Category result = categoryService.getCategoryById(0L);

        Assertions.assertNotNull(result);
        verify(categoryRepositories, times(1)).findById(0L);
    }
}
