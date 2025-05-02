package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.ProductRepositories;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
import com.Rest.CategoryProduct.pubsub.ProductPubSubPublisher;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements  ProductService{

//    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductPubSubPublisher pubSubPublisher;
    @Autowired
    private ProductRepositories productRepo;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public Product getProductById(Long id) {
        log.info("Requesting to fetch the product record with id {}",id);
        Optional<Product> findById = Optional.ofNullable(productRepo.findById(id)
                .orElseThrow(() -> {
                    log.info("Product Does not found with id : {}", id);
                    return new ResourceNotFoundExceptions("Product Does not found with id : " + id);
                }));

        Product product = findById.get();
        log.info("Product found with id : {}",id);

//      pub/sub
        String message = String.format("{\"event\":\"GET_PRODUCT\", \"id\":%d, \"name\":\"%s\" }",id,product.getProductName());
        pubSubPublisher.publish(message);
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        log.info("Requesting to fetch all the products");

        List<Product> products = productRepo.findAll();
        if (products.isEmpty()){
            log.warn("No products found in the database.");
            return  products;
        }

        log.info("Fetched {} products",products.size());
//      pub/sub
        String message = String.format("{\"event\":\"GET_All_PRODUCTS\", \"Count\":%s}",products.size());
        pubSubPublisher.publish(message);
        return products;
    }

    @Override
    public String insertProduct(Product product) {
        log.info("Request received to create a new product with name : {}",product.getProductName());
        Optional<Product> categoryExist = productRepo.findByProductName(product.getProductName());
        if(categoryExist.isPresent()){
            log.warn("{} product already exists !!!",product.getProductName());
            return product.getProductName()+" Product already exists";
        }
        productRepo.save(product);
        log.info("Successfully created a new product : {}", product.getProductName());

        // Using pub-sub
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "PRODUCT_CREATED");
        jsonObject.addProperty("id", product.getProductId());
        jsonObject.addProperty("name", product.getProductName());

        String message = jsonObject.toString();

//        String message = String.format("{\"event\":\"PRODUCT_CREATED\", \"id\":%d, \"name\":\"%s\" }",product.getProductId(),product.getProductName());
        pubSubPublisher.publish(message);

        return "Data inserted successfully";
    }

    @Override
    public String updateProduct(Product product, Long id) {
        log.info("Request received to update the product with id : {}",id);
        Optional<Product> productExist = Optional.ofNullable(productRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("product Not Found With Id : {}", id);
                    return new ResourceNotFoundExceptions("product Does Not Found With Id : "+ id);
                }));
        /*if(!productExist.isPresent()){
            return "There is no such product";
        }*/

        Product product1 = productExist.get();
        product1.setProductName(product.getProductName());
        productRepo.save(product1);
        log.info("Updated a product with id : {}",id);

        // Using pub/sub
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "PRODUCT_UPDATED");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", product.getProductName());

        String message = jsonObject.toString();
        pubSubPublisher.publish(message);
        return "Data updated successfully";
    }

    @Override
    public String deleteProduct(Long id) {
/*//        In case if product does not eixst - you can add this instead of handling exception
        Optional<Product> productExist = productRepo.findById(id);
        if(!productExist.isPresent()){
            return "There is no such category";
        }*/

        log.info("Request received to delete the product with id : {}",id);
        Product productExist = productRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("product Not Found With Id : {}",id);
                    return new ResourceNotFoundExceptions("Product Does Not Found With Id : "+id);
                });

//        Using Exception handling
        productRepo.deleteById(id);
        log.info("Deleted product with id : {}",id);

      // Using pub/sub
        String message = String.format("{\"event\":\"PRODUCT_DELETED\", \"id\":%d, \"name\":\"%s\"}",id,productExist.getProductName());
        pubSubPublisher.publish(message);

//        resetAutoIncrement();
        return "Data deleted successfully";
    }

//    @Transactional
//    public void resetAutoIncrement(){
//        entityManager.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate();
//    }
}
