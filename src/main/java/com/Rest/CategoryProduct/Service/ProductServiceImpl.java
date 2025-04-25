package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.ProductRepositories;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
import com.Rest.CategoryProduct.pubsub.PubSubPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements  ProductService{

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private PubSubPublisher pubSubPublisher;
    @Autowired
    private ProductRepositories productRepo;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public Product getProductById(Long id) {
        logger.info("Requesting to fetch the record {}",id);
        Optional<Product> findById = productRepo.findById(id);
        if (findById.isPresent()){
            logger.info("Product found with id : {}",id);
            return findById.get();
        }else {
            logger.info("Product Does not found with id : {}",id);
            return null;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        logger.info("Requesting to fetch all the products");
        logger.info("Fetched all the products");
        return productRepo.findAll();
    }

    @Override
    public String insertProduct(Product product) {
        logger.info("Request received to create a new product with name : {}",product.getProductName());
        Optional<Product> categoryExist = productRepo.findByProductName(product.getProductName());
        if(categoryExist.isPresent()){
            logger.error("{} product already exists !!!",product.getProductName());
            return "Product already exists";
        }
        productRepo.save(product);
        logger.info("Successfully created a new product : {}", product.getProductName());

        // Using pub-sub
        String message = String.format("\"event\":\"PRODUCT_CREATED\", \"id\":%d, \"name\":\"%s\" ",product.getProductId(),product.getProductName());
        pubSubPublisher.publish(message);
        return "Data inserted successfully";
    }

    @Override
    public String updateProduct(Product product, Long id) {
        logger.info("Request received to update the product with id : {}",id);
        Optional<Product> productExist = Optional.ofNullable(productRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("product Not Found With Id : {}", id);
                    return new ResourceNotFoundExceptions("product Does Not Found With Id : {}" + id);
                }));
        /*if(!productExist.isPresent()){
            return "There is no such product";
        }*/

        Product product1 = productExist.get();
        product1.setProductName(product.getProductName());
        productRepo.save(product1);
        logger.info("Updated a product with id : {}",id);
        return "Data updates successfully";
    }

    @Override
    public String deleteProduct(Long id) {
/*//        In case if product does not eixst - you can add this instead of handling exception
        Optional<Product> productExist = productRepo.findById(id);
        if(!productExist.isPresent()){
            return "There is no such category";
        }*/

        logger.info("Request received to delete the product with id : {}",id);
        Product productExist = productRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("product Not Found With Id : {}",id);
                    return new RuntimeException("Product Does Not Found With Id : "+id);
                });

//        Using Exception handling
        productRepo.deleteById(id);
        logger.info("Deleted product with id : {}",id);

//        resetAutoIncrement();
        return "Data deleted successfully";
    }

//    @Transactional
//    public void resetAutoIncrement(){
//        entityManager.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate();
//    }
}
