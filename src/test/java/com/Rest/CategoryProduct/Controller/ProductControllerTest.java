package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Service.ProductService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductById() {
        Product product = new Product(1l,"iPhone",null);
        when(productService.getProductById(1L)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("iPhone", response.getBody().getProductName());
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product(1L, "iPhone", null);
        Product product2 = new Product(2L, "Galaxy", null);
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateProduct() {
        Product newProduct = new Product(1L, "Pixel", null);
        when(productService.insertProduct(newProduct)).thenReturn("Product inserted successfully");

        ResponseEntity<String> response = productController.createProduct(newProduct);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product inserted successfully", response.getBody());
        assertTrue(response.getBody().contains("inserted"));
    }

    @Test
    void testUpdateProduct() {
        Product updatedProduct = new Product(1L, "Updated Product", null);
        when(productService.updateProduct(updatedProduct, 1L)).thenReturn("Product updated successfully");

        ResponseEntity<String> response = productController.updateProduct(updatedProduct, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully", response.getBody());
    }

    @Test
    void testDeleteProduct() {
        when(productService.deleteProduct(1L)).thenReturn("Product deleted successfully");

        ResponseEntity<String> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertEquals("Product deleted successfully", response.getBody());
    }
}
