package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.ProductRepositories;
import com.Rest.CategoryProduct.Service.Impl.ProductServiceImpl;
import com.Rest.CategoryProduct.pubsub.product.ProductPubSubPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepositories productRepositories;

    @Mock
    private ProductPubSubPublisher pubSubPublisher;

    @InjectMocks
    private ProductServiceImpl productService;

    private List<Product> mockProducts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create Products
        Product p1 = new Product(1L, "MacBook air", null);
        Product p2 = new Product(2L, "iPhone 16 pro", null);
        Product p3 = new Product(3L, "Samsung S24", null);

        mockProducts = List.of(p1, p2, p3);
    }

    @Test
    @DisplayName("Get the product by ID")
    void testGetProductById() {
        Product product = mockProducts.get(0);
        when(productRepositories.findById(1L)).thenReturn(Optional.of(product));

        Product result = assertDoesNotThrow(() -> productService.getProductById(1L));

        assertNotNull(result);
        assertEquals("MacBook air", result.getProductName());
        verify(productRepositories, times(1)).findById(1L);
        verify(pubSubPublisher, times(1)).publish(contains("GET_PRODUCT"));
    }

    @Test
    @DisplayName("Get product by ID - not found")
    void testGetProductById_ThrowsException() {
        Long id = 999L;
        when(productRepositories.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundExceptions.class, () -> productService.getProductById(id));
        verify(productRepositories, times(1)).findById(id);
        verify(pubSubPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Return all products")
    void testGetAllProduct() {
        when(productRepositories.findAll()).thenReturn(mockProducts);

        List<Product> result = productService.getAllProducts();
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(productRepositories, times(1)).findAll();
        verify(pubSubPublisher, times(1)).publish(contains("GET_All_PRODUCTS"));
    }

    @Test
    @DisplayName("Return empty list when no products found")
    void testGetAllProduct_IfNoProductFound() {
        when(productRepositories.findAll()).thenReturn(new ArrayList<>());

        List<Product> result = productService.getAllProducts();
        assertTrue(result.isEmpty());
        verify(productRepositories, times(1)).findAll();
        verify(pubSubPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Insert a new product successfully")
    void testInsertProduct() {
        Product product = new Product(4L, "Oppo", null);
        when(productRepositories.save(product)).thenReturn(product);

        String result = productService.insertProduct(product);
        assertNotNull(result);
        assertEquals("Product inserted successfully", result);
        verify(productRepositories, times(1)).save(product);
        verify(pubSubPublisher, times(1)).publish(contains("PRODUCT_CREATED"));
    }

    @Test
    @DisplayName("Insert product - duplicate name")
    void testInsertProduct_ForDuplicateProduct() {
        Product existingProduct = mockProducts.get(0);
        Product product = new Product(5L, existingProduct.getProductName(), null);
        String name = product.getProductName();
        when(productRepositories.findByProductName(name)).thenReturn(Optional.of(existingProduct));

        String result = productService.insertProduct(product);
        assertNotNull(result);
        assertEquals(name + " Product already exists", result);

        verify(productRepositories, times(1)).findByProductName(name);
        verify(pubSubPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Update an existing product")
    void testUpdateProduct() {
        Long productId = 1L;
        Product existingProduct = new Product(productId, "Old Name", null);
        Product updatedProduct = new Product(productId, "Updated Name", null);

        when(productRepositories.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepositories.save(any(Product.class))).thenReturn(updatedProduct);

        String result = productService.updateProduct(updatedProduct, productId);

        assertEquals("Product updated successfully", result);
        assertEquals("Updated Name", existingProduct.getProductName());

        verify(productRepositories, times(1)).findById(productId);
        verify(productRepositories, times(1)).save(updatedProduct);
        verify(pubSubPublisher, times(1)).publish(contains("PRODUCT_UPDATED"));
    }

    @Test
    @DisplayName("Update product - not found should throw exception and not publish")
    void testUpdateProduct_ThrowsException() {
        Long productId = 999L;
        Product updated = new Product(productId, "NonExistent", null);

        when(productRepositories.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundExceptions.class,
                () -> productService.updateProduct(updated, productId));
        verify(pubSubPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Delete an existing product")
    void testDeleteProduct_whenProductExist() {
        Product product = mockProducts.get(0);
        Long productId = product.getProductId();

        when(productRepositories.findById(productId)).thenReturn(Optional.of(product));
        String result = productService.deleteProduct(productId);

        assertEquals("Product deleted successfully", result);
        verify(productRepositories).deleteById(productId);
        verify(pubSubPublisher, times(1)).publish(contains("PRODUCT_DELETED"));
    }

    @Test
    @DisplayName("Delete product - not found should throw exception and not publish")
    void testDeleteProduct_whenProductDoesNotExist_shouldThrowException() {
        Long productId = 999L;
        when(productRepositories.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundExceptions.class,
                () -> productService.deleteProduct(productId));
        verify(productRepositories, never()).deleteById(productId);
        verify(productRepositories, times(1)).findById(productId);
        verify(pubSubPublisher, never()).publish(any());
    }
}
