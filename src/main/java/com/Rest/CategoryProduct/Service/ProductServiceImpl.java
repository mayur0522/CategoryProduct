package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Repositories.ProductRepositories;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements  ProductService{

    @Autowired
    private ProductRepositories productRepo;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public Product getProductById(Long id) {
        Optional<Product> findById = productRepo.findById(id);
        if (findById.isPresent()){
            return findById.get();
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public String insertProduct(Product product) {
        Optional<Product> categoryExist = productRepo.findByProductName(product.getProductName());
        if(categoryExist.isPresent()){
            return "Product already exists";
        }
        productRepo.save(product);
        return "Data inserted successfully";
    }

    @Override
    public String updateProduct(Product product, Long id) {
        Optional<Product> productExist = productRepo.findById(id);
        if(!productExist.isPresent()){
            return "There is no such product";
        }

        Product product1 = productExist.get();
        product1.setProductName(product.getProductName());
        productRepo.save(product1);
        return "Data updates successfully";
    }

    @Override
    public String deleteProduct(Long id) {
////        In case if product does not eixst - you can add this instead of handling exception
//        Optional<Product> productExist = productRepo.findById(id);
//        if(!productExist.isPresent()){
//            return "There is no such category";
//        }

        Product productExist = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Does Not Found With Id : "+id));

//        Using Exception handling
        productRepo.deleteById(id);
//        resetAutoIncrement();
        return "Data deleted successfully";
    }

//    @Transactional
//    public void resetAutoIncrement(){
//        entityManager.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate();
//    }
}
