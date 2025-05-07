package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.BrandRepositories;
import com.Rest.CategoryProduct.pubsub.BrandPubSubPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepositories brandRepositories;

    @Mock
    private BrandPubSubPublisher pubSubPublisher;

    @InjectMocks
    private BrandServiceImpl brandService;

    List<Brand> mockBrands;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create Products
        Product p1= new Product(1,"MacBook air", null);
        Product p2= new Product(2,"Iphone 16 pro", null);
        Product p3= new Product(3,"Samsung S24", null);

        // Create brands
        Brand Apple = new Brand(1l,"Apple",null,new ArrayList<>(List.of(p1,p2)));
        Brand Samsung = new Brand(2l, "Samsung", null, new ArrayList<>(List.of(p3)));
        Brand Adidas = new Brand(3l, "Adidas", null, new ArrayList<>());

        //Product is linked to brand
        p1.setBrand(Apple);
        p2.setBrand(Apple);
        p3.setBrand(Adidas);

        // Save the mock list
        mockBrands = List.of(Apple,Samsung,Adidas);
    }

    @Test
    void testGetBrandById() {
        Brand brand = mockBrands.get(0);
        when(brandRepositories.findById(1l)).thenReturn(Optional.ofNullable(brand));

        Brand result = Assertions.assertDoesNotThrow(()->brandService.getBrandById(1l));

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result == null);
        Assertions.assertTrue("Apple" == result.getBrandName());
        verify(brandRepositories,times(1)).findById(1l);
        verify(pubSubPublisher,times(1)).publish(contains("GET_BRAND"));
    }

    @Test
    void testGetBrandById_ThrowsException() {
        Long id = 999l;
        when(brandRepositories.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundExceptions.class, ()->{
            Brand result = brandService.getBrandById(id);
        });
        verify(brandRepositories,times(1)).findById(id);
        verify(pubSubPublisher,never()).publish(any());
    }
    @Test
    void testGetAllBrand() {
        when(brandRepositories.findAll()).thenReturn(mockBrands);

        List<Brand> result = brandService.getAllBrand();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.size() > 0);
        verify(brandRepositories,times(1)).findAll();
        verify(pubSubPublisher,times(1)).publish(contains("GET_All_BRANDS"));
    }

    @Test
    void testGetAllBrand_IfNoCategoryFound() {
        when(brandRepositories.findAll()).thenReturn(new ArrayList<>());

        List<Brand> result = brandService.getAllBrand();
        Assertions.assertTrue(result.size() == 0);
        verify(brandRepositories,times(1)).findAll();
        verify(pubSubPublisher,never()).publish(any());
    }

    @Test
    void testInsertBrand() {
        Brand brand = new Brand(3l,"Oppo",null,new ArrayList<>());
        when(brandRepositories.save(brand)).thenReturn(brand);

        String result = brandService.insertBrand(brand);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result == null);
        Assertions.assertNotEquals(brand.getBrandName()+" Brand already exists",result);
        Assertions.assertTrue(result == "Brand inserted successfully");
        Assertions.assertEquals("Brand inserted successfully",result);
        verify(brandRepositories,times(1)).save(brand);
        verify(pubSubPublisher,times(1)).publish(contains("BRAND_CREATED"));
    }

    @Test
    void testInsertBrand_ForDuplicateCategory() {
        Brand existingBrand = mockBrands.get(0);
        Brand brand = new Brand(3l,"Oppo",null,new ArrayList<>());
        String name = brand.getBrandName();
        when(brandRepositories.findByBrandName(name)).thenReturn(Optional.ofNullable(existingBrand));

        String result = brandService.insertBrand(brand);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(name+" Brand already exists",result);
        verify(brandRepositories,times(1)).findByBrandName(name);
        verify(pubSubPublisher,times(0)).publish(any());
    }

    @Test
    void testUpdateBrand() {
        Long brandId = 1L;
        Brand existingBrand = new Brand(brandId, "Old Brand",null, new ArrayList<>());
        Brand updatedBrand = new Brand(brandId, "Updated Brand",null, new ArrayList<>());

        // Mock repository behaviour
        when(brandRepositories.findById(brandId)).thenReturn(Optional.of(existingBrand));
        when(brandRepositories.save(any(Brand.class))).thenReturn(updatedBrand);

        // Perform update
        String result = brandService.updateBrand(updatedBrand, brandId);

        // Assertions
        Assertions.assertEquals("Brand updated successfully", result);
        Assertions.assertEquals("Updated Brand", existingBrand.getBrandName());

        // Verify repository and publisher interactions
        verify(brandRepositories, times(1)).findById(brandId);
        verify(brandRepositories, times(1)).save(updatedBrand);
        verify(pubSubPublisher, times(1)).publish(contains("BRAND_UPDATED"));
    }

    @Test
    void testUpdateBrand_ThrowsException(){
        Long brandId = 999L;
        Brand updated = new Brand(brandId, "updated brand",null, new ArrayList<>());

        when(brandRepositories.findById(brandId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundExceptions.class,
                () -> brandService.updateBrand(updated, brandId));
        verify(pubSubPublisher, never()).publish(any());
    }

    @Test
    void testDeleteBrand_whenCategoryExist(){
        Brand brand = mockBrands.get(0);
        Long brandId = brand.getBrandId();

        when(brandRepositories.findById(brandId)).thenReturn(Optional.of(brand));
        String result = brandService.deleteBrand(brandId);

        // Assert
        Assertions.assertEquals("Brand deleted successfully", result);
        verify(brandRepositories).deleteById(brandId);
        verify(pubSubPublisher,times(1)).publish(contains("BRAND_DELETED"));
    }

    @Test
    void testDeleteBrand_whenBrandDoesNotExist_shouldThrowException() {
        // Invalid ID
        Long brandId = 999L;
        when(brandRepositories.findById(brandId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundExceptions.class,
                () -> brandService.deleteBrand(brandId)
        );
        verify(brandRepositories,never()).deleteById(brandId);
        verify(brandRepositories,times(1)).findById(brandId);
        verify(pubSubPublisher,never()).publish(any());
    }
}
