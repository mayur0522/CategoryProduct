package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.CategoryRepositories;
/*import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;*/
import com.Rest.CategoryProduct.pubsub.CategoryPubSubPublisher;
import com.Rest.CategoryProduct.pubsub.ProductPubSubPublisher;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryPubSubPublisher pubSubPublisher;
    @Autowired
    private CategoryRepositories categoryRepo;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public Category getCategoryById(Long id) {
        logger.info("Requesting to fetch the record {}",id);
        Optional<Category> findById = Optional.of(categoryRepo.findById(id)
                .orElseThrow(()->{
                    logger.info("Category does not found with id : {}",id);
                    return new ResourceNotFoundExceptions("Category does not found with id : "+id);
                }));

        Category category = findById.get();
        logger.info("Category found with id : {}",id);

        // Using pub/sub
        String message = String.format("{\"event\":\"GET_CATEGORY\", \"id\":%d, \"name\":\"%s\"}",id,category.getCategoryName());
        pubSubPublisher.publish(message);
        return category;

    }

    @Override
    public List<Category> getAllCategory() {
        logger.info("Requesting to fetch all the categories");

        List<Category> categories = categoryRepo.findAll();
        if (categories.isEmpty()){
            logger.warn("No categories found in the database.");
            return categories;
        }

        logger.info("Fetched {} categories",categories.size());
        // Using pub/sub
        String message = String.format("{\"event\":\"GET_All_CATEGORIES\", \"Count\":%s}",categories.size());
        pubSubPublisher.publish(message);
        return categoryRepo.findAll();
    }

    @Override
    public String insertCategory(Category category) {
        logger.info("Request received to create a new category with name : {}",category.getCategoryName());
        Optional<Category> categoryExist = categoryRepo.findByCategoryName(category.getCategoryName());
        if(categoryExist.isPresent()){
            logger.error("{} category already exists !!!",category.getCategoryName());
            return category.getCategoryName()+" Category already exists";
        }
        categoryRepo.save(category);
        logger.info("Successfully created a new category : {}",category.getCategoryName());

        // Using pub-sub
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "CATEGORY_CREATED");
        jsonObject.addProperty("id", category.getCategoryId());
        jsonObject.addProperty("name", category.getCategoryName());

        String message = jsonObject.toString();
        pubSubPublisher.publish(message);

        return "Category inserted successfully";
    }

    @Override
    public String updateCategory(Category category, Long id) {
        logger.info("Request received to update the category with id : {}",id);
        Optional<Category> categoryExist = Optional.ofNullable(categoryRepo.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Category Not Found With Id : {}",id);
                    return new ResourceNotFoundExceptions("category Does Not Found With Id : {}" + id);
                }));

        /*if(!categoryExist.isPresent()){
            logger.error("Category Not Found !!! ");
            return "There is no such category";
        }*/

        Category category1 = categoryExist.get();
        category1.setCategoryName(category.getCategoryName());
        categoryRepo.save(category1);
        logger.info("Updated a category with id : {}",id);

        // Using pub-sub
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "CATEGORY_UPDATED");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", category.getCategoryName());

        String message = jsonObject.toString();
        pubSubPublisher.publish(message);
        return "Data updated successfully";
    }

    @Override
    public String deleteCategory(Long id) {
/*        Optional<Category> categoryExist = categoryRepo.findById(id);
        if(!categoryExist.isPresent()){
            return "There is no such category";
        }*/

        logger.info("Request received to delete the category with id : {}",id);
        Category categoryExist = categoryRepo.findById(id)
                .orElseThrow(() ->{
                    logger.error("Category Not Found With Id : {}",id);
                    return new ResourceNotFoundExceptions("category Does Not Found With Id : "+id);
                });
        categoryRepo.deleteById(id);
        logger.info("Deleted category with id : {}",id);

        // Using pub/sub
        String message = String.format("{\"event\":\"CATEGORY_DELETED\", \"id\":%d, \"name\":\"%s\"}",id,categoryExist.getCategoryName());
        pubSubPublisher.publish(message);

//        resetAutoIncrement();
        return "Category deleted successfully";
    }

/*    @Transactional
    public void resetAutoIncrement(){
        entityManager.createNativeQuery("ALTER TABLE category AUTO_INCREMENT = 1").executeUpdate();
    }*/
}
