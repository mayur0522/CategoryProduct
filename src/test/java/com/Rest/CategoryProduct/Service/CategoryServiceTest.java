package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.CategoryRepositories;
import com.Rest.CategoryProduct.Service.Impl.CategoryServiceImpl;
import com.Rest.CategoryProduct.pubsub.category.CategoryPubSubPublisher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepositories categoryRepositories;

    @Mock
    private CategoryPubSubPublisher pubSubPublisher;
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
        Category Clothing = new Category(2l,"Clothing", new ArrayList<>(List.of(Adidas)));

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
    @DisplayName("Get the name of category using id")
    void testGetCategoryById(){
        Category category = mockCategories.get(0);
        when(categoryRepositories.findById(1L)).thenReturn(java.util.Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Electronics",result.getCategoryName());
        verify(categoryRepositories, times(1)).findById(1L);
        verify(pubSubPublisher,times(1)).publish(contains("GET_CATEGORY"));
    }

    @Test
    void testGetCategoryById_ThrowsException(){
        Long categoryId = 999l;
        when(categoryRepositories.findById(categoryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundExceptions.class, ()-> categoryService.getCategoryById(categoryId));
        verify(pubSubPublisher,times(0)).publish(any());
    }

    @Test
    @DisplayName("It should return all the categories from repository")
    void testGetAllCategories(){
//        Do not use like this
//        when(categoryService.getAllCategory()).thenReturn(mockCategories);

//        as we are testing service layer use like this
        when(categoryRepositories.findAll()).thenReturn(mockCategories);
        List<Category> result = categoryService.getAllCategory();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,result.size());
        Assertions.assertEquals("Electronics", result.get(0).getCategoryName());
        verify(categoryRepositories, times(1)).findAll();
        verify(pubSubPublisher,times(1)).publish(contains("GET_All_CATEGORIES"));
    }

    @Test
    void testGetAllCategories_IfNoCategoryFound(){
        when(categoryRepositories.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = categoryService.getAllCategory();
//       It ensures your service method returns a valid (non-null) object, even if it's empty.
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        verify(categoryRepositories,times(1)).findAll();
        verify(pubSubPublisher,never()).publish(any());
    }

    @Test
    void testInsertCategory(){
        Category category = new Category(3,"Accessories",null);
        when(categoryRepositories.save(category)).thenReturn(category);

        Assertions.assertDoesNotThrow(()->{
            String result = categoryService.insertCategory(category);

            Assertions.assertFalse(result.isEmpty());
            Assertions.assertNotNull(result);
            Assertions.assertEquals("Category inserted successfully", result);
        });
        verify(categoryRepositories, times(1)).save(category);
        verify(pubSubPublisher,times(1)).publish(contains("CATEGORY_CREATED"));
    }

    @Test
    void testInsertCategory_ForDuplicateCategory(){
        Category category = mockCategories.get(0);
        Category newCategory = new Category(11l,"Electronics",new ArrayList<>());
        String newCategoryName = newCategory.getCategoryName();
        when(categoryRepositories.findByCategoryName(newCategoryName)).thenReturn(Optional.ofNullable(category));

        String result = categoryService.insertCategory(newCategory);
        Assertions.assertNotNull(result);

        verify(categoryRepositories,times(1)).findByCategoryName(newCategoryName);
        verify(pubSubPublisher,never()).publish(any());
    }
    @Test
    void testUpdateCategory() {
        Long categoryId = 1L;
        Category existingCategory = new Category(categoryId, "Old Name", new ArrayList<>());
        Category updatedCategory = new Category(categoryId, "Updated Name", new ArrayList<>());

        when(categoryRepositories.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepositories.save(any(Category.class))).thenReturn(updatedCategory);

        String result = categoryService.updateCategory(updatedCategory, categoryId);
        Assertions.assertEquals("Category updated successfully", result);
        Assertions.assertEquals("Updated Name", existingCategory.getCategoryName());

        verify(categoryRepositories, times(1)).findById(categoryId);
        verify(categoryRepositories, times(1)).save(updatedCategory);
        verify(pubSubPublisher, times(1)).publish(contains("CATEGORY_UPDATED"));
    }

    @Test
    void testUpdateCategory_ThrowsException(){
        Long categoryId = 999l;
        Category category = new Category(categoryId,"Updated category",new ArrayList<>());
        when(categoryRepositories.findById(categoryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundExceptions.class, ()-> categoryService.updateCategory(category,categoryId));
        verify(pubSubPublisher,never()).publish(any());
    }
    @Test
    void testDeleteCategory_whenCategoryExist(){
        Category category = mockCategories.get(0);
        Long categoryId = category.getCategoryId();

        when(categoryRepositories.findById(categoryId)).thenReturn(Optional.of(category));
        String result = categoryService.deleteCategory(categoryId);

        // Assert
        Assertions.assertEquals("Category deleted successfully", result);
        verify(categoryRepositories).deleteById(categoryId);
        verify(pubSubPublisher,times(1)).publish(contains("CATEGORY_DELETED"));
    }

    @Test
    void testDeleteCategory_whenCategoryDoesNotExist_shouldThrowException() {
        // Invalid ID
        Long categoryId = 999L;
        when(categoryRepositories.findById(categoryId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundExceptions.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepositories,never()).deleteById(categoryId);
        verify(categoryRepositories,times(1)).findById(categoryId);
        verify(pubSubPublisher,never()).publish(any());
    }
}
