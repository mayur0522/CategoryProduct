package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Service.BrandService;
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
class BrandControllerTest {

    @InjectMocks
    private BrandController brandController;

    @Mock
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBrandById() {
        Brand brand = new Brand(1L, "Nike", null,new ArrayList<>());
        when(brandService.getBrandById(1L)).thenReturn(brand);

        ResponseEntity<Brand> response = brandController.getBrandById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nike", response.getBody().getBrandName());
    }

    @Test
    void testGetAllBrands() {
        Brand brand1 = new Brand(1L, "Nike",null,new ArrayList<>());
        Brand brand2 = new Brand(2L, "Adidas", null,new ArrayList<>());
        when(brandService.getAllBrand()).thenReturn(Arrays.asList(brand1, brand2));

        ResponseEntity<List<Brand>> response = brandController.getAllBrand();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateBrand() {
        Brand newBrand = new Brand(1L, "Puma", null, new ArrayList<>());
        when(brandService.insertBrand(newBrand)).thenReturn("Brand inserted successfully");

        ResponseEntity<String> response = brandController.createBrand(newBrand);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Brand inserted successfully", response.getBody());
        assertTrue(response.getBody().startsWith("Brand"));
    }

    @Test
    void testUpdateBrand() {
        Brand updatedBrand = new Brand(1L, "UpdatedBrand", null,new ArrayList<>());
        when(brandService.updateBrand(updatedBrand, 1L)).thenReturn("Brand updated successfully");

        ResponseEntity<String> response = brandController.updateBrand(updatedBrand, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Brand updated successfully", response.getBody());
        assertFalse(response.getBody().endsWith("Brand"));
        assertTrue(response.getBody().endsWith("successfully"));
    }

    @Test
    void testDeleteBrand() {
        when(brandService.deleteBrand(1L)).thenReturn("Brand deleted successfully");

        ResponseEntity<String> response = brandController.deleteBrand(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Brand deleted successfully", response.getBody());
    }
}
